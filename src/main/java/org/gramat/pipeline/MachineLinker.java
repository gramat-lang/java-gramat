package org.gramat.pipeline;

import lombok.extern.slf4j.Slf4j;
import org.gramat.data.actions.Actions;
import org.gramat.data.actions.ActionsW;
import org.gramat.data.nodes.Nodes;
import org.gramat.errors.ErrorFactory;
import org.gramat.graphs.Direction;
import org.gramat.graphs.Graph;
import org.gramat.graphs.links.Link;
import org.gramat.graphs.links.LinkAction;
import org.gramat.graphs.links.LinkEmpty;
import org.gramat.graphs.links.LinkSymbol;
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
    private final Map<String, Segment> segments;
    private final Map<Node, Node> nodeMap;

    private MachineLinker(Map<String, Machine> dependencies) {
        this.dependencies = dependencies;
        this.graph = new Graph(IdentifierProvider.create(1));
        this.segments = new HashMap<>();
        this.nodeMap = new HashMap<>();
    }

    private Machine run(Machine main) {
        log.debug("Linking main machine...");

        resolveMachine(main, null);

        log.debug("Linking completed: {} link(s)", graph.links.getCount());

        var source = unmap(main.source);
        var targets = unmap(main.targets);
        return new Machine(source, targets, graph.links);
    }

    private void resolveMachine(Machine machine, String name) {
        log.debug("Resolving {} machine...", name != null ? name : "main");

        for (var link : machine.links) {
            var newSource = map(link.source);
            var newTarget = map(link.target);
            if (link instanceof LinkSymbol linkSym) {
                if (linkSym.symbol instanceof SymbolReference ref) {
                    var dependency = findDependency(ref.name);

                    linkMachine(
                            newSource, newTarget,
                            ref.name, dependency,
                            linkSym.beginActions, linkSym.endActions);
                }
                else {
                    graph.createLink(newSource, newTarget, linkSym.symbol, linkSym.beginActions, linkSym.endActions);
                }
            }
            else if (link instanceof LinkEmpty linkEmp) {
                graph.createLink(newSource, newTarget, linkEmp.beginActions, linkEmp.endActions);
            }
            else {
                throw ErrorFactory.notImplemented();
            }
        }
    }

    private void linkMachine(Node newSource, Node newTarget, String name, Machine dependency, ActionsW beginActions, ActionsW endActions) {
        var segment = segments.get(name);
        if (segment == null) {
            var source = map(dependency.source);
            var targets = map(dependency.targets);

            segment = new Segment(source, targets);

            segments.put(name, segment);

            resolveMachine(dependency, name);
        }

        graph.createEnter(
                newSource, segment.source, name,
                beginActions, endActions);
        graph.createExit(
                segment.targets, newTarget, name,
                beginActions, endActions);
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

        var dir = Direction.compute(depLink, dependency.source, dependency.targets);
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

    private Nodes unmap(Nodes oldNodes) {
        var newNodes = Nodes.createW();

        for (var oldNode : oldNodes) {
            newNodes.add(unmap(oldNode));
        }

        return newNodes;
    }

    private Nodes map(Nodes oldNodes) {
        var newNodes = Nodes.createW();

        for (var oldNode : oldNodes) {
            newNodes.add(map(oldNode));
        }

        return newNodes;
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
