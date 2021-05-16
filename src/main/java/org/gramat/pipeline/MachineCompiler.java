package org.gramat.pipeline;

import lombok.extern.slf4j.Slf4j;
import org.gramat.actions.Action;
import org.gramat.graphs.Automaton;
import org.gramat.graphs.Graph;
import org.gramat.graphs.Link;
import org.gramat.graphs.Machine;
import org.gramat.graphs.Node;
import org.gramat.graphs.NodeSet;
import org.gramat.tools.IdentifierProvider;
import org.gramat.tools.NodeSetQueue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class MachineCompiler {

    public static Automaton compile(Machine machine) {
        return new MachineCompiler().run(machine);
    }

    private final Graph graph;
    private final Map<String, Set<Node>> idClosures;
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
        var queue = new NodeSetQueue();

        var closure0 = Link.forwardClosure(machine.source, machine.links);

        queue.add(closure0);

        while (!queue.isEmpty()) {
            var oldSources = queue.remove();
            var oldSourcesId = NodeSet.id(oldSources);
            if (control.add(oldSourcesId)) {
                log.debug("PROCESSING CLOSURE {}", oldSourcesId);
                var newSource = map(oldSources, oldSourcesId);

                for (var symbol : symbols) {
                    var oldLinks = Link.forwardClosure(oldSources, symbol, machine.links);
                    if (!oldLinks.isEmpty()) {
                        var oldTargets = Link.collectTargets(oldLinks);
                        var oldTargetsId = NodeSet.id(oldTargets);
                        var newTarget = map(oldTargets, oldTargetsId);
                        var beginActions = new HashSet<Action>();  // TODO implement actions
                        var endActions = new HashSet<Action>();

                        graph.createLink(newSource, newTarget, symbol, null, beginActions, endActions);

                        queue.add(oldTargets);
                    }
                }
            }
        }

        log.debug("Compiling machine completed");

        return createAutomaton(closure0, machine);
    }

    private Automaton createAutomaton(Set<Node> sourceClosure, Machine machine) {
        var initial = idNewNodes.get(NodeSet.id(sourceClosure));
        var accepted = new NodeSet();

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

    private Node map(Set<Node> nodes, String id) {
        return idNewNodes.computeIfAbsent(id, k -> {
            var newNode = graph.createNode();
            idClosures.put(id, nodes);
            return newNode;
        });
    }

}
