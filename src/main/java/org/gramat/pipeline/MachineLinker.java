package org.gramat.pipeline;

import lombok.extern.slf4j.Slf4j;
import org.gramat.actions.ActionFactory;
import org.gramat.data.Actions;
import org.gramat.errors.ErrorFactory;
import org.gramat.graphs.Direction;
import org.gramat.graphs.Graph;
import org.gramat.graphs.Link;
import org.gramat.graphs.LinkAction;
import org.gramat.graphs.LinkEmpty;
import org.gramat.graphs.LinkSymbol;
import org.gramat.graphs.Machine;
import org.gramat.graphs.MachineProgram;
import org.gramat.graphs.Node;
import org.gramat.graphs.Segment;
import org.gramat.symbols.SymbolFactory;
import org.gramat.symbols.SymbolReference;
import org.gramat.tools.IdentifierProvider;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MachineLinker {

    public static Machine run(MachineProgram program) {
        return new MachineLinker(program.dependencies).run(program.main);
    }

    private final Graph graph;
    private final Map<String, Machine> dependencies;
    private final Map<String, Segment> segments;  // TODO are segments here really useful? (replace with Set<String>?)
    private final Map<Node, Node> nodeMap;

    private MachineLinker(Map<String, Machine> dependencies) {
        this.dependencies = dependencies;
        this.graph = new Graph(IdentifierProvider.create(1));
        this.segments = new HashMap<>();
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
        for (var link : machine.links) {
            var newSource = map(link.source);
            var newTarget = map(link.target);
            if (link instanceof LinkSymbol linkSym) {
                if (linkSym.symbol instanceof SymbolReference ref) {
                    var dependency = findDependency(ref.name);
                    var segment = segments.get(ref.name);
                    if (segment == null) {
                        createSegment(
                                newSource, newTarget,
                                ref.name, dependency,
                                linkSym.beginActions, linkSym.endActions);
                    }
                    else {
                        connectSegment(
                                newSource, newTarget,
                                ref.name, dependency, segment,
                                linkSym.beginActions, linkSym.endActions);
                    }
                }
                else {
                    graph.createLink(newSource, newTarget, linkSym.symbol, linkSym.beginActions, linkSym.endActions);
                }
            }
            else if (link instanceof LinkEmpty) {
                graph.createLink(newSource, newTarget);
            }
            else {
                throw ErrorFactory.notImplemented();
            }
        }
    }

    private void createSegment(Node rootSource, Node rootTarget, String name, Machine dependency, Actions rootBeginActions, Actions rootEndActions) {
        log.debug("Creating segment {}...", name);

        // Define segment of the reference
        segments.put(name, new Segment(rootSource, rootTarget));

        // Recursive links first
        for (var depLink : dependency.links) {
            var linkInfo = computeLinkInfo(depLink, dependency, rootSource, rootTarget, rootBeginActions, rootEndActions);
            if (depLink instanceof LinkSymbol linkSym) {
                if (linkSym.symbol instanceof SymbolReference ref) {
                    var nestedDependency = findDependency(ref.name);
                    var nestedSegment = segments.get(ref.name);
                    if (nestedSegment == null) {
                        createSegment(
                                linkInfo.newSource, linkInfo.newTarget,
                                ref.name, nestedDependency,
                                linkInfo.beginActions, linkInfo.endActions);
                    }
                    else {
                        connectSegment(
                                linkInfo.newSource, linkInfo.newTarget,
                                ref.name, nestedDependency, nestedSegment,
                                linkInfo.beginActions, linkInfo.endActions);
                    }
                }
                else {
                    graph.createLink(
                            linkInfo.newSource, linkInfo.newTarget,
                            linkSym.symbol,
                            linkInfo.beginActions, linkInfo.endActions);
                }
            }
            else if (depLink instanceof LinkEmpty) {
                graph.createLink(linkInfo.newSource, linkInfo.newTarget);
            }
            else {
                throw ErrorFactory.notImplemented();
            }
        }

        log.debug("Creating segment completed: {}", name);
    }

    private void connectSegment(Node newSource, Node newTarget, String name, Machine dependency, Segment segment, Actions beginActions, Actions endActions) {
        log.debug("Connecting segment {}...", name);

        var pushAction = ActionFactory.push(name);
        var popAction = ActionFactory.pop(name);

        // TODO consider closures for adding the actions

        for (var fromLink : Link.forwardSymbols(dependency.source, dependency.links)) {
            var linkTarget = map(fromLink.target);

            // TODO validate if SymbolReference should be created/connected here as well
            graph.createLink(
                    newSource, linkTarget,
                    fromLink.symbol,
                    Actions.join(beginActions, fromLink.beginActions),
                    Actions.join(fromLink.endActions, pushAction));
        }

        for (var toLink : Link.backwardSymbols(dependency.target, dependency.links)) {
            var linkSource = map(toLink.source);
            // TODO validate if SymbolReference should be created/connected here as well
            graph.createLink(
                    linkSource, newTarget,
                    SymbolFactory.token(toLink.symbol, name),
                    toLink.beginActions,
                    Actions.join(toLink.endActions, endActions, popAction));
        }

        log.debug("Connecting segment completed: {}", name);
    }

    private RecursiveLinkInfo computeLinkInfo(Link depLink, Machine dependency, Node rootSource, Node rootTarget, Actions rootBeginActions, Actions rootEndActions) {
        Node newSource;
        Node newTarget;
        Actions beginActions;
        Actions endActions;
        Actions linkBeginActions;
        Actions linkEndActions;

        if (depLink instanceof LinkAction linkAct) {
            linkBeginActions = linkAct.beginActions;
            linkEndActions = linkAct.endActions;
        }
        else {
            linkBeginActions = Actions.empty();
            linkEndActions = Actions.empty();
        }

        var dir = Direction.compute(depLink, dependency.source, dependency.target);
        if (dir == Direction.S_S) {
            newSource = rootSource;
            newTarget = rootSource;
            beginActions = Actions.join(rootBeginActions, linkBeginActions);
            endActions = linkEndActions;
        }
        else if (dir == Direction.S_T) {
            newSource = rootSource;
            newTarget = rootTarget;
            beginActions = Actions.join(rootBeginActions, linkBeginActions);
            endActions = Actions.join(linkEndActions, rootEndActions);
        }
        else if (dir == Direction.S_N) {
            newSource = rootSource;
            newTarget = map(depLink.target);
            beginActions = Actions.join(rootBeginActions, linkBeginActions);
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
            endActions = Actions.join(linkEndActions, rootEndActions);
        }
        else if (dir == Direction.T_N) {
            newSource = rootTarget;
            newTarget = map(depLink.target);
            beginActions = linkBeginActions;
            endActions = linkEndActions;
        }
        else if (dir == Direction.N_S) {
            newSource = map(depLink.source);
            newTarget = rootSource;
            beginActions = linkBeginActions;
            endActions = linkEndActions;
        }
        else if (dir == Direction.N_T) {
            newSource = map(depLink.source);
            newTarget = rootTarget;
            beginActions = linkBeginActions;
            endActions = Actions.join(linkEndActions, rootEndActions);
        }
        else if (dir == Direction.N_N) {
            newSource = map(depLink.source);
            newTarget = map(depLink.target);
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

            log.debug("MAP {} -> {}", oldNode, newNode);

            newNode.wildcard = oldNode.wildcard;

            return newNode;
        });
    }

    private static class RecursiveLinkInfo {
        public final Node newSource;
        public final Node newTarget;
        public final Actions beginActions;
        public final Actions endActions;
        public RecursiveLinkInfo(Node newSource, Node newTarget, Actions beginActions, Actions endActions) {
            this.newSource = newSource;
            this.newTarget = newTarget;
            this.beginActions = beginActions;
            this.endActions = endActions;
        }
    }

}
