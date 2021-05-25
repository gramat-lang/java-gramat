package org.gramat.pipeline;

import org.gramat.actions.ActionFactory;
import org.gramat.data.actions.Actions;
import org.gramat.data.nodes.Nodes;
import org.gramat.graphs.CleanMachine;
import org.gramat.graphs.CleanMachineProgram;
import org.gramat.graphs.Machine;
import org.gramat.graphs.Segment;
import org.gramat.graphs.DirtyMachine;
import org.gramat.graphs.Node;
import org.gramat.graphs.NodeProvider;
import org.gramat.graphs.links.LinkProvider;
import org.gramat.symbols.Symbol;
import org.gramat.symbols.SymbolFactory;
import org.gramat.symbols.SymbolReference;

import java.util.HashMap;
import java.util.Map;

public class MachineCompiler {


    public static CleanMachine run(NodeProvider nodeProvider, CleanMachineProgram program) {
        return new MachineCompiler(program.dependencies, nodeProvider).run(program.main);
    }

    private final Map<String, CleanMachine> dependencies;
    private final LinkProvider linkProvider;
    private final NodeProvider nodeProvider;
    private final Map<String, Segment> segments;

    private MachineCompiler(Map<String, CleanMachine> dependencies, NodeProvider nodeProvider) {
        this.dependencies = dependencies;
        this.nodeProvider = nodeProvider;
        this.linkProvider = new LinkProvider();
        this.segments = new HashMap<>();
    }

    private CleanMachine run(CleanMachine main) {
        var segment = compile(main);
        return MachineCleaner.run(nodeProvider,
                Nodes.of(segment.source()),
                segment.targets(),
                linkProvider.toList());
    }

    private Segment compile(CleanMachine machine) {
        for (var link : machine.links()) {
            if (link.getSymbol() instanceof SymbolReference ref) {
                var dependency = dependencies.get(ref.name);
                if (dependency == null) {
                    throw new RuntimeException();
                }
                var segment = compile(ref.name, dependency,
                        link.getBeforeActions(), link.getAfterActions());

                // Connect segment to the main machine
                linkProvider.createLink(link.getSource(), segment.source());
                linkProvider.createLink(segment.targets(), link.getTarget());
            }
            else {
                linkProvider.addLink(link);
            }
        }

        return new Segment(machine.source(), machine.targets());
    }

    private Segment compile(String name, CleanMachine machine, Actions rootBeforeActions, Actions rootAfterActions) {
        var nodeMap = new HashMap<Node, Node>();
        var newSource = nodeProvider.createNode();
        var newTargets = Nodes.createW();

        nodeMap.put(machine.source(), newSource);

        for (var target : machine.targets()) {
            var newTarget = nodeProvider.createNode();

            nodeMap.put(target, newTarget);

            newTargets.add(newTarget);
        }

        var result = new Segment(newSource, newTargets);

        segments.put(name, result);

        for (var link : machine.links()) {
            var linkSource = nodeMap.getOrDefault(link.getSource(), link.getSource());
            var linkTarget = nodeMap.getOrDefault(link.getTarget(), link.getTarget());
            var fromSource = (newSource == linkSource);
            var toTarget = (newTargets.contains(linkTarget));

            Actions beforeActions;
            Actions afterActions;

            if (fromSource) {
                beforeActions = Actions.join(rootBeforeActions, link.getBeforeActions());
            }
            else {
                beforeActions = link.getBeforeActions();
            }

            if (toTarget) {
                afterActions = Actions.join(rootAfterActions, link.getAfterActions());
            }
            else {
                afterActions = link.getAfterActions();
            }


            if (fromSource && !toTarget) {
                beforeActions = Actions.join(ActionFactory.push(name), beforeActions);
            }

            if (toTarget && !fromSource) {
                afterActions = Actions.join(afterActions, ActionFactory.pop(name));
            }

            if (link.getSymbol() instanceof SymbolReference ref) {
                var segment = segments.get(ref.name);
                if (segment != null) {
                    linkProvider.createLink(link.getSource(), segment.source());
                    linkProvider.createLink(segment.targets(), link.getTarget());
                }
                else {
                    var dependency = dependencies.get(ref.name);
                    if (dependency == null) {
                        throw new RuntimeException();
                    }

                    segment = compile(
                            ref.name, dependency,
                            beforeActions, afterActions);

                    // Connect segment to the main machine
                    linkProvider.createLink(link.getSource(), segment.source());
                    linkProvider.createLink(segment.targets(), link.getTarget());
                }
            }
            else {
                Symbol symbol;

                if (newTargets.contains(linkTarget) && (fromSource ^ toTarget)) {
                    symbol = SymbolFactory.token(link.getSymbol(), name);
                }
                else {
                    symbol = link.getSymbol();
                }

                var newLink = linkProvider.createLink(linkSource, linkTarget, symbol);
                newLink.addBeforeActions(beforeActions);
                newLink.addAfterActions(afterActions);
            }
        }

        return result;
    }
}
