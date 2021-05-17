package org.gramat.pipeline;

import lombok.extern.slf4j.Slf4j;
import org.gramat.data.Actions;
import org.gramat.data.ActionsW;
import org.gramat.data.Nodes;
import org.gramat.graphs.Automaton;
import org.gramat.graphs.Graph;
import org.gramat.graphs.Link;
import org.gramat.graphs.LinkSymbol;
import org.gramat.graphs.Machine;
import org.gramat.graphs.Node;
import org.gramat.tools.IdentifierProvider;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Slf4j
public class MachineCompiler {

    public static Automaton compile(Machine machine) {
        return new MachineCompiler().run(machine);
    }

    private final Graph graph;
    private final Map<String, Nodes> idClosures;
    private final Map<String, Node> idNewNodes;

    private MachineCompiler() {
        graph = new Graph(IdentifierProvider.create(1));
        idClosures = new HashMap<>();
        idNewNodes = new HashMap<>();
    }

    private Automaton run(Machine machine) {
        log.debug("Compiling machine...");

        var symbols = machine.getSymbols();
        var control = new HashSet<String>();
        var queue = new ArrayDeque<Nodes>();

        var closure0 = Link.forwardClosure(machine.source, machine.links);

        queue.add(closure0);

        while (!queue.isEmpty()) {
            var oldSources = queue.remove();
            var oldSourcesId = oldSources.getId();
            if (control.add(oldSourcesId)) {
                log.debug("PROCESSING CLOSURE {}", oldSourcesId);
                var newSource = map(oldSources, oldSourcesId);

                for (var symbol : symbols) {
                    var oldLinks = Link.forwardSymbols(oldSources, symbol, machine.links);
                    if (!oldLinks.isEmpty()) {
                        var oldTargets = Link.collectTargets(oldLinks);
                        var oldTargetsId = oldTargets.getId();
                        var newTarget = map(oldTargets, oldTargetsId);
                        var beginActions = Actions.createW();
                        var endActions = Actions.createW();

                        createActions(oldSources, oldTargets, oldLinks, beginActions, endActions);

                        graph.createLink(newSource, newTarget, symbol, beginActions, endActions);

                        queue.add(oldTargets);
                    }
                }
            }
        }

        log.debug("Compiling machine completed");

        return createAutomaton(closure0, machine);
    }

    private void createActions(Nodes sources, Nodes targets, List<LinkSymbol> links, ActionsW beginActions, ActionsW endActions) {
        for (var link : links) {
            if (sources.contains(link.source)) {
                beginActions.append(link.beginActions);
            }

            if (targets.contains(link.target)) {
                endActions.prepend(link.endActions);
            }
        }
    }

    private Automaton createAutomaton(Nodes sourceClosure, Machine machine) {
        var initial = idNewNodes.get(sourceClosure.getId());
        var accepted = Nodes.createW();

        var targetClosure = Link.backwardClosure(machine.target, machine.links);

        for (var entry : idClosures.entrySet()) {
            for (var target : targetClosure) {
                if (entry.getValue().contains(target)) {
                    var newAccepted = idNewNodes.get(entry.getKey());

                    accepted.add(newAccepted);
                }
            }
        }

        return new Automaton(initial, accepted, graph.links);
    }

    private Node map(Nodes nodes, String id) {
        return idNewNodes.computeIfAbsent(id, k -> {
            var newNode = graph.createNode();
            idClosures.put(id, nodes);
            return newNode;
        });
    }

}
