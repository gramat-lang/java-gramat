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

    public static MachineContract run(MachineProgram program) {
        return new MachineLinker(program.dependencies).run(program.main);
    }

    private final Graph graph;
    private final Map<String, Machine> dependencies;
    private final GraphMapper mapper;
    private final Map<String, RecursiveSegment> recursiveSegments;

    private MachineLinker(Map<String, Machine> dependencies) {
        this.dependencies = dependencies;
        this.graph = new Graph();
        this.mapper = new GraphMapper(graph);
        this.recursiveSegments = new HashMap<>();
    }

    private MachineContract run(Machine main) {
        resolveMachine(main);

        var sources = mapper.unmap(main.source);
        var targets = mapper.unmap(main.targets);
        return new MachineContract(sources, targets, graph.links);
    }

    private void resolveMachine(Machine machine) {
        var nonRecursiveLinks = new ArrayList<Link>();

        // First, only copy recursive links
        for (var link : machine.links) {
            if (link instanceof LinkReference) {
                var linkRef = (LinkReference) link;
                var recursiveSegment = recursiveSegments.get(linkRef.name);
                if (recursiveSegment != null) {
                    log.debug("Connecting segment {}", linkRef.name);
                    connectSegment(
                            linkRef.name,
                            mapper.map(linkRef.source), mapper.map(linkRef.target),
                            linkRef.beginActions, linkRef.endActions,
                            recursiveSegment);
                }
                else {
                    log.debug("Creating segment {}", linkRef.name);

                    var dependency = findDependency(linkRef.name);
                    var newSources = mapper.map(link.source);
                    var newTargets = mapper.map(dependency.targets);

                    recursiveSegments.put(linkRef.name, new RecursiveSegment(newSources, newTargets));

                    mapper.map(link.target, newTargets);
                    mapper.map(newSources, link.source);

                    resolveReference(linkRef.name,
                            newSources, newTargets, dependency,
                            linkRef.beginActions, linkRef.endActions);
                }
            }
            else {
                nonRecursiveLinks.add(link);
            }
        }

        // Then, copy non-recursive
        for (var link : nonRecursiveLinks) {
            var newSources = mapper.map(link.source);
            var newTargets = mapper.map(link.target);

            if (link instanceof LinkSymbol) {
                var linkSymbol = (LinkSymbol) link;

                graph.createLink(newSources, newTargets, linkSymbol.symbol, null, linkSymbol.beginActions, linkSymbol.endActions);
            }
            else if (link instanceof LinkEmpty) {
                graph.createLink(newSources, newTargets);
            }
            else {
                throw ErrorFactory.notImplemented();
            }
        }
    }

    private void resolveReference(String name, Set<Node> rootSources, Set<Node> rootTargets, Machine machine, Set<Action> rootBeginActions, Set<Action> rootEndActions) {
        var nonRecursiveLinks = new ArrayList<Link>();

        // Recursive links first
        for (var link : machine.links) {
            if (link instanceof LinkReference) {
                var linkRef = (LinkReference) link;
                var linkInfo = computeLinkInfo(linkRef, machine, rootSources, rootTargets, rootBeginActions, rootEndActions);
                var recursiveSegment = recursiveSegments.get(linkRef.name);
                if (recursiveSegment != null) {
                    connectSegment(linkRef.name,
                            linkInfo.newSources, linkInfo.newTargets,
                            linkInfo.beginActions, linkInfo.endActions,
                            recursiveSegment);
                }
                else {
                    recursiveSegments.put(linkRef.name, new RecursiveSegment(linkInfo.newSources, linkInfo.newTargets));

                    resolveReference(
                            linkRef.name,
                            linkInfo.newSources, linkInfo.newTargets,
                            findDependency(linkRef.name),
                            linkInfo.beginActions, linkInfo.endActions);
                }
            }
            else {
                nonRecursiveLinks.add(link);
            }
        }

        for (var link : nonRecursiveLinks) {
            var linkInfo = computeLinkInfo(link, machine, rootSources, rootTargets, rootBeginActions, rootEndActions);
            if (link instanceof LinkSymbol) {
                var linkSymbol = (LinkSymbol) link;
                graph.createLink(
                        linkInfo.newSources, linkInfo.newTargets,
                        linkSymbol.symbol, null,
                        linkInfo.beginActions, linkInfo.endActions);
            }
            else if (link instanceof LinkEmpty) {
                graph.createLink(linkInfo.newSources, linkInfo.newTargets);
            }
            else {
                throw ErrorFactory.notImplemented();
            }
        }
    }

    private void connectSegment(String name, Set<Node> newSources, Set<Node> newTargets, Set<Action> beginActions, Set<Action> endActions, RecursiveSegment recursiveSegment) {
        for (var fromLink : Link.findFrom(recursiveSegment.sources, graph.links)) {
            if (fromLink instanceof LinkReference) {
                var linkRef = (LinkReference) fromLink;
                graph.createLink(
                        newSources, fromLink.target,
                        linkRef.name, name,
                        DataUtils.join(beginActions, linkRef.beginActions),
                        linkRef.endActions);
            }
            else if (fromLink instanceof LinkSymbol) {
                var linkSym = (LinkSymbol) fromLink;
                graph.createLink(
                        newSources, fromLink.target,
                        linkSym.symbol, name,
                        DataUtils.join(beginActions, linkSym.beginActions),
                        linkSym.endActions);
            }
            else if (fromLink instanceof LinkEmpty) {
                graph.createLink(newSources, fromLink.target);
            }
            else {
                throw ErrorFactory.notImplemented();
            }
        }

        for (var toLink : Link.findTo(recursiveSegment.targets, graph.links)) {
            if (toLink instanceof LinkReference) {
                var linkRef = (LinkReference) toLink;
                graph.createLink(
                        linkRef.source, newTargets,
                        linkRef.name, name,
                        linkRef.beginActions,
                        DataUtils.join(linkRef.endActions, endActions));
            }
            else if (toLink instanceof LinkSymbol) {
                var linkSym = (LinkSymbol) toLink;
                graph.createLink(
                        linkSym.source, newTargets,
                        linkSym.symbol, name,
                        linkSym.beginActions,
                        DataUtils.join(linkSym.endActions, endActions));
            }
            else if (toLink instanceof LinkEmpty) {
                graph.createLink(toLink.source, newTargets);
            }
            else {
                throw ErrorFactory.notImplemented();
            }
        }
    }

    private RecursiveLinkInfo computeLinkInfo(Link link, Machine machine, Set<Node> rootSources, Set<Node> rootTargets, Set<Action> rootBeginActions, Set<Action> rootEndActions) {
        Set<Node> newSources;
        Set<Node> newTargets;
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
            newSources = rootSources;
            newTargets = rootSources;
            beginActions = DataUtils.join(rootBeginActions, linkBeginActions);
            endActions = linkEndActions;
        }
        else if (dir == Direction.S_T) {
            newSources = rootSources;
            newTargets = rootTargets;
            beginActions = DataUtils.join(rootBeginActions, linkBeginActions);
            endActions = linkEndActions;
        }
        else if (dir == Direction.S_N) {
            newSources = rootSources;
            newTargets = mapper.map(link.target);
            beginActions = DataUtils.join(rootBeginActions, linkBeginActions);
            endActions = linkEndActions;
        }
        else if (dir == Direction.T_S) {
            newSources = rootTargets;
            newTargets = rootSources;
            beginActions = linkBeginActions;
            endActions = linkEndActions;
        }
        else if (dir == Direction.T_T) {
            newSources = rootTargets;
            newTargets = rootTargets;
            beginActions = linkBeginActions;
            endActions = linkEndActions;
        }
        else if (dir == Direction.T_N) {
            newSources = rootTargets;
            newTargets = mapper.map(link.target);
            beginActions = linkBeginActions;
            endActions = linkEndActions;
        }
        else if (dir == Direction.N_S) {
            newSources = mapper.map(link.source);
            newTargets = rootSources;
            beginActions = linkBeginActions;
            endActions = linkEndActions;
        }
        else if (dir == Direction.N_T) {
            newSources = mapper.map(link.source);
            newTargets = rootTargets;
            beginActions = linkBeginActions;
            endActions = linkEndActions;
        }
        else if (dir == Direction.N_N) {
            newSources = mapper.map(link.source);
            newTargets = mapper.map(link.target);
            beginActions = linkBeginActions;
            endActions = linkEndActions;
        }
        else {
            throw ErrorFactory.internalError("not implemented: " + dir);
        }

        return new RecursiveLinkInfo(newSources, newTargets, beginActions, endActions);
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
        var fromTarget = (machine.targets.contains(link.source));
        var toSource = (machine.source == link.target);
        var toTarget = (machine.targets.contains(link.target));

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

    private static class RecursiveLinkInfo {
        public final Set<Node> newSources;
        public final Set<Node> newTargets;
        public final Set<Action> beginActions;
        public final Set<Action> endActions;
        public RecursiveLinkInfo(Set<Node> newSources, Set<Node> newTargets, Set<Action> beginActions, Set<Action> endActions) {
            this.newSources = newSources;
            this.newTargets = newTargets;
            this.beginActions = beginActions;
            this.endActions = endActions;
        }
    }

    private static class RecursiveSegment {
        public final Set<Node> sources;
        public final Set<Node> targets;
        public RecursiveSegment(Set<Node> sources, Set<Node> targets) {
            this.sources = sources;
            this.targets = targets;
        }
    }

    private enum Direction {
        S_S, S_T, S_N,
        T_S, T_T, T_N,
        N_S, N_T, N_N,
    }

}
