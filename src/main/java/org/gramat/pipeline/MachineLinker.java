package org.gramat.pipeline;

import lombok.extern.slf4j.Slf4j;
import org.gramat.actions.Action;
import org.gramat.errors.ErrorFactory;
import org.gramat.machines.Graph;
import org.gramat.machines.GraphMapper;
import org.gramat.machines.Link;
import org.gramat.machines.LinkAction;
import org.gramat.machines.LinkEmpty;
import org.gramat.machines.LinkReference;
import org.gramat.machines.LinkSymbol;
import org.gramat.machines.Machine;
import org.gramat.machines.MachineContract;
import org.gramat.machines.MachineProgram;
import org.gramat.machines.Node;
import org.gramat.tools.DataUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class MachineLinker {

    public static Machine run(MachineProgram program) {
        return new MachineLinker(program.dependencies).run(program.main);
    }

    private final Graph graph;
    private final Map<String, Machine> dependencies;
    private final Map<String, RecursiveSegment> recursiveSegments;
    private final Map<Node, Node> nodeMap;

    private MachineLinker(Map<String, Machine> dependencies) {
        this.dependencies = dependencies;
        this.graph = new Graph();
        this.recursiveSegments = new HashMap<>();
        this.nodeMap = new HashMap<>();
    }

    private Machine run(Machine main) {
        log.debug("Linking main machine...");

        resolveMachine(main);

        log.debug("Linking completed: {} link(s)", graph.links.size());

        var source = unmap(main.source);
        var target = unmap(main.target);
        return new Machine(source, target, graph.links);
    }

    private void resolveMachine(Machine machine) {
        var nonRecursiveLinks = new ArrayList<Link>();

        // First, only copy recursive links
        for (var link : machine.links) {
            if (link instanceof LinkReference) {
                var linkRef = (LinkReference) link;
                var newSource = map(link.source);
                var newTarget = map(link.target);
                var recursiveSegment = recursiveSegments.get(linkRef.name);
                if (recursiveSegment != null) {
                    log.debug("Connecting segment {}", linkRef.name);
                    connectSegment(
                            linkRef.name,
                            newSource, newTarget,
                            linkRef.beginActions, linkRef.endActions,
                            recursiveSegment);
                }
                else {
                    log.debug("Creating segment {}", linkRef.name);

                    var dependency = findDependency(linkRef.name);

                    recursiveSegments.put(linkRef.name, new RecursiveSegment(newSource, newTarget));

                    resolveReference(linkRef.name,
                            newSource, newTarget, dependency,
                            linkRef.beginActions, linkRef.endActions);
                }
            }
            else {
                nonRecursiveLinks.add(link);
            }
        }

        // Then, copy non-recursive
        for (var link : nonRecursiveLinks) {
            var newSource = map(link.source);
            var newTarget = map(link.target);

            if (link instanceof LinkSymbol) {
                var linkSym = (LinkSymbol) link;

                graph.createLink(newSource, newTarget, linkSym.symbol, null, linkSym.beginActions, linkSym.endActions);
            }
            else if (link instanceof LinkEmpty) {
                graph.createLink(newSource, newTarget);
            }
            else {
                throw ErrorFactory.notImplemented();
            }
        }
    }

    private void resolveReference(String name, Node rootSource, Node rootTarget, Machine machine, Set<Action> rootBeginActions, Set<Action> rootEndActions) {
        var nonRecursiveLinks = new ArrayList<Link>();

        // Recursive links first
        for (var link : machine.links) {
            if (link instanceof LinkReference) {
                var linkRef = (LinkReference) link;
                var linkInfo = computeLinkInfo(linkRef, machine, rootSource, rootTarget, rootBeginActions, rootEndActions);
                var recursiveSegment = recursiveSegments.get(linkRef.name);
                if (recursiveSegment != null) {
                    connectSegment(linkRef.name,
                            linkInfo.newSource, linkInfo.newTarget,
                            linkInfo.beginActions, linkInfo.endActions,
                            recursiveSegment);
                }
                else {
                    recursiveSegments.put(linkRef.name, new RecursiveSegment(linkInfo.newSource, linkInfo.newTarget));

                    resolveReference(
                            linkRef.name,
                            linkInfo.newSource, linkInfo.newTarget,
                            findDependency(linkRef.name),
                            linkInfo.beginActions, linkInfo.endActions);
                }
            }
            else {
                nonRecursiveLinks.add(link);
            }
        }

        for (var link : nonRecursiveLinks) {
            var linkInfo = computeLinkInfo(link, machine, rootSource, rootTarget, rootBeginActions, rootEndActions);
            if (link instanceof LinkSymbol) {
                var linkSym = (LinkSymbol) link;
                graph.createLink(
                        linkInfo.newSource, linkInfo.newTarget,
                        linkSym.symbol, null,
                        linkInfo.beginActions, linkInfo.endActions);
            }
            else if (link instanceof LinkEmpty) {
                graph.createLink(linkInfo.newSource, linkInfo.newTarget);
            }
            else {
                throw ErrorFactory.notImplemented();
            }
        }
    }

    private void connectSegment(String name, Node newSource, Node newTarget, Set<Action> beginActions, Set<Action> endActions, RecursiveSegment recursiveSegment) {
        for (var fromLink : Link.findFrom(recursiveSegment.source, graph.links)) {
            if (fromLink instanceof LinkReference) {
                var linkRef = (LinkReference) fromLink;
                graph.createLink(
                        newSource, fromLink.target,
                        linkRef.name, name,
                        DataUtils.join(beginActions, linkRef.beginActions),
                        linkRef.endActions);
            }
            else if (fromLink instanceof LinkSymbol) {
                var linkSym = (LinkSymbol) fromLink;
                graph.createLink(
                        newSource, fromLink.target,
                        linkSym.symbol, name,
                        DataUtils.join(beginActions, linkSym.beginActions),
                        linkSym.endActions);
            }
            else if (fromLink instanceof LinkEmpty) {
                graph.createLink(newSource, fromLink.target);
            }
            else {
                throw ErrorFactory.notImplemented();
            }
        }

        for (var toLink : Link.findTo(recursiveSegment.target, graph.links)) {
            if (toLink instanceof LinkReference) {
                var linkRef = (LinkReference) toLink;
                graph.createLink(
                        linkRef.source, newTarget,
                        linkRef.name, name,
                        linkRef.beginActions,
                        DataUtils.join(linkRef.endActions, endActions));
            }
            else if (toLink instanceof LinkSymbol) {
                var linkSym = (LinkSymbol) toLink;
                graph.createLink(
                        linkSym.source, newTarget,
                        linkSym.symbol, name,
                        linkSym.beginActions,
                        DataUtils.join(linkSym.endActions, endActions));
            }
            else if (toLink instanceof LinkEmpty) {
                graph.createLink(toLink.source, newTarget);
            }
            else {
                throw ErrorFactory.notImplemented();
            }
        }
    }

    private RecursiveLinkInfo computeLinkInfo(Link link, Machine machine, Node rootSource, Node rootTarget, Set<Action> rootBeginActions, Set<Action> rootEndActions) {
        Node newSource;
        Node newTarget;
        Set<Action> beginActions;
        Set<Action> endActions;
        Set<Action> linkBeginActions;
        Set<Action> linkEndActions;

        if (link instanceof LinkAction) {
            var linkAct = (LinkAction) link;
            linkBeginActions = linkAct.beginActions;
            linkEndActions = linkAct.endActions;
        }
        else {
            linkBeginActions = Set.of();
            linkEndActions = Set.of();
        }

        var dir = computeDirection(link, machine);
        if (dir == Direction.S_S) {
            newSource = rootSource;
            newTarget = rootSource;
            beginActions = DataUtils.join(rootBeginActions, linkBeginActions);
            endActions = linkEndActions;
        }
        else if (dir == Direction.S_T) {
            newSource = rootSource;
            newTarget = rootTarget;
            beginActions = DataUtils.join(rootBeginActions, linkBeginActions);
            endActions = linkEndActions;
        }
        else if (dir == Direction.S_N) {
            newSource = rootSource;
            newTarget = map(link.target);
            beginActions = DataUtils.join(rootBeginActions, linkBeginActions);
            endActions = linkEndActions;
        }
        else if (dir == Direction.T_S) {
            newSource = rootTarget;
            newTarget = rootSource;
            beginActions = linkBeginActions;
            endActions = linkEndActions;
        }
        else if (dir == Direction.T_T) {
            newSource = rootTarget;
            newTarget = rootTarget;
            beginActions = linkBeginActions;
            endActions = linkEndActions;
        }
        else if (dir == Direction.T_N) {
            newSource = rootTarget;
            newTarget = map(link.target);
            beginActions = linkBeginActions;
            endActions = linkEndActions;
        }
        else if (dir == Direction.N_S) {
            newSource = map(link.source);
            newTarget = rootSource;
            beginActions = linkBeginActions;
            endActions = linkEndActions;
        }
        else if (dir == Direction.N_T) {
            newSource = map(link.source);
            newTarget = rootTarget;
            beginActions = linkBeginActions;
            endActions = linkEndActions;
        }
        else if (dir == Direction.N_N) {
            newSource = map(link.source);
            newTarget = map(link.target);
            beginActions = linkBeginActions;
            endActions = linkEndActions;
        }
        else {
            throw ErrorFactory.internalError("not implemented: " + dir);
        }

        return new RecursiveLinkInfo(newSource, newTarget, beginActions, endActions);
    }

    private Machine findDependency(String name) {
        var dependency = dependencies.get(name);
        if (dependency == null) {
            throw ErrorFactory.notFound("missing dependency: " + name);
        }
        return dependency;
    }

    private Direction computeDirection(Link link, Machine machine) {
        var fromSource = (machine.source == link.source);
        var fromTarget = (machine.target == link.source);
        var toSource = (machine.source == link.target);
        var toTarget = (machine.target == link.target);

        if (fromSource) {
            if (toSource) {
                return Direction.S_S;
            } else if (toTarget) {
                return Direction.S_T;
            } else {
                return Direction.S_N;
            }
        }
        else if (fromTarget) {
            if (toSource) {
                return Direction.T_S;
            } else if (toTarget) {
                return Direction.T_T;
            } else {
                return Direction.T_N;
            }
        }
        else {
            if (toSource) {
                return Direction.N_S;
            } else if (toTarget) {
                return Direction.N_T;
            } else {
                return Direction.N_N;
            }
        }
    }

    private Node unmap(Node oldNode) {
        var newNode = nodeMap.get(oldNode);

        if (newNode == null) {
            throw ErrorFactory.internalError("node not mapped");
        }

        return newNode;
    }

    private Node map(Node oldNode) {
        return nodeMap.computeIfAbsent(oldNode, k -> {
            var newNode = graph.createNode();

            newNode.wildcard = oldNode.wildcard;

            return newNode;
        });
    }

    private static class RecursiveLinkInfo {
        public final Node newSource;
        public final Node newTarget;
        public final Set<Action> beginActions;
        public final Set<Action> endActions;
        public RecursiveLinkInfo(Node newSource, Node newTarget, Set<Action> beginActions, Set<Action> endActions) {
            this.newSource = newSource;
            this.newTarget = newTarget;
            this.beginActions = beginActions;
            this.endActions = endActions;
        }
    }

    private static class RecursiveSegment {
        public final Node source;
        public final Node target;
        public RecursiveSegment(Node source, Node target) {
            this.source = source;
            this.target = target;
        }
    }

    private enum Direction {
        S_S, S_T, S_N,
        T_S, T_T, T_N,
        N_S, N_T, N_N,
    }

}
