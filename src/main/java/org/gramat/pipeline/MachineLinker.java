package org.gramat.pipeline;

import org.gramat.data.actions.Actions;
import org.gramat.data.nodes.Nodes;
import org.gramat.graphs.CleanMachine;
import org.gramat.graphs.CleanMachineProgram;
import org.gramat.graphs.CleanSegment;
import org.gramat.graphs.DirtyMachine;
import org.gramat.graphs.Node;
import org.gramat.graphs.NodeProvider;
import org.gramat.graphs.links.LinkProvider;
import org.gramat.graphs.links.LinkSymbol;
import org.gramat.symbols.SymbolReference;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MachineLinker {

    public static CleanMachine run(NodeProvider nodeProvider, CleanMachineProgram program) {
        var linkProvider = new LinkProvider();
        var segment = compile(nodeProvider, linkProvider, program.dependencies, program.main);
        return MachineCompiler.compile(nodeProvider, new DirtyMachine(Nodes.of(segment.source()), segment.targets(), linkProvider.toList()));
    }

    private static CleanSegment compile(NodeProvider nodeProvider, LinkProvider linkProvider, Map<String, CleanMachine> dependencies, CleanMachine machine) {
        for (var link : machine.links()) {
            if (link.getSymbol() instanceof SymbolReference ref) {
                var dependency = dependencies.get(ref.name);
                if (dependency == null) {
                    throw new RuntimeException();
                }
                var segment = compile(nodeProvider, linkProvider, dependencies, dependency,
                        link.getBeforeActions(), link.getAfterActions());

                // Connect segment to the main machine
                linkProvider.createLink(link.getSource(), segment.source());
                linkProvider.createLink(segment.targets(), link.getTarget());
            }
            else {
                linkProvider.addLink(link);
            }
        }

        return new CleanSegment(machine.source(), machine.targets());
    }

    private static CleanSegment compile(NodeProvider nodeProvider, LinkProvider linkProvider, Map<String, CleanMachine> dependencies, CleanMachine machine, Actions rootBeforeActions, Actions rootAfterActions) {
        var nodeMap = new HashMap<Node, Node>();
        var newSource = nodeProvider.createNode();
        var newTargets = Nodes.createW();

        nodeMap.put(machine.source(), newSource);

        for (var target : machine.targets()) {
            var newTarget = nodeProvider.createNode();

            nodeMap.put(target, newTarget);

            newTargets.add(newTarget);
        }

        for (var link : machine.links()) {
            var linkSource = nodeMap.getOrDefault(link.getSource(), link.getSource());
            var linkTarget = nodeMap.getOrDefault(link.getTarget(), link.getTarget());

            Actions beforeActions;
            Actions afterActions;

            if (newSource == linkSource) {
                beforeActions = Actions.join(rootBeforeActions, link.getBeforeActions());
            }
            else {
                beforeActions = link.getBeforeActions();
            }

            if (newTargets.contains(linkTarget)) {
                afterActions = Actions.join(rootAfterActions, link.getAfterActions());
            }
            else {
                afterActions = link.getAfterActions();
            }

            if (link.getSymbol() instanceof SymbolReference ref) {
                var dependency = dependencies.get(ref.name);
                if (dependency == null) {
                    throw new RuntimeException();
                }

                var segment = compile(nodeProvider, linkProvider,
                        dependencies, dependency,
                        beforeActions, afterActions);

                // Connect segment to the main machine
                linkProvider.createLink(link.getSource(), segment.source());
                linkProvider.createLink(segment.targets(), link.getTarget());
            }
            else {
                var newLink = linkProvider.createLink(linkSource, linkTarget, link.getSymbol());
                newLink.addAfterActions(afterActions);
                newLink.addBeforeActions(beforeActions);
            }
        }

        return new CleanSegment(newSource, newTargets);
    }
}
