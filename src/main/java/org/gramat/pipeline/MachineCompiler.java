package org.gramat.pipeline;

import lombok.extern.slf4j.Slf4j;
import org.gramat.data.nodes.Nodes;
import org.gramat.graphs.Automaton;
import org.gramat.graphs.ClosureMapper;
import org.gramat.graphs.Graph;
import org.gramat.graphs.Machine;
import org.gramat.tools.IdentifierProvider;

import java.util.ArrayDeque;
import java.util.HashSet;

@Slf4j
public class MachineCompiler {

    public static Automaton compile(Machine machine) {
        return new MachineCompiler().run(machine);
    }

    private final Graph graph;
    private final ClosureMapper mapper;

    private MachineCompiler() {
        graph = new Graph(IdentifierProvider.create(1000));
        mapper = new ClosureMapper(graph);
    }

    private Automaton run(Machine machine) {
        log.debug("Compiling machine...");

        var symbols = machine.getSymbols();
        var control = new HashSet<String>();
        var queue = new ArrayDeque<Nodes>();

        var closure0 = machine.links.forwardClosure(machine.source);

        queue.add(closure0);

        while (!queue.isEmpty()) {
            var oldSources = queue.remove();
            var oldSourcesId = oldSources.getId();
            if (control.add(oldSourcesId)) {
                var newSource = mapper.map(oldSources, oldSourcesId);

                for (var symbol : symbols) {
                    var oldLinks = machine.links.findFrom(oldSources, symbol);
                    if (oldLinks.isPresent()) {
                        var oldLinksTargets = oldLinks.collectTargets();
                        var oldTargets = machine.links.forwardClosure(oldLinksTargets);
                        var oldTargetsId = oldTargets.getId();
                        var newTarget = mapper.map(oldTargets, oldTargetsId);

                        log.debug("NEW LINK {} -> {}: {}", oldSources, oldTargets, symbol);

                        graph.createLink(newSource, newTarget, symbol);

                        queue.add(oldTargets);
                    }
                }
            }
        }

        log.debug("Compiling machine completed");

        return createAutomaton(closure0, machine);
    }

    private Automaton createAutomaton(Nodes sourceClosure, Machine machine) {
        var initial = mapper.unmap(sourceClosure);
        var targetClosure = machine.links.backwardClosure(machine.target);
        var accepted = mapper.searchNodes(targetClosure);
        return new Automaton(initial, accepted, graph.links);
    }

}
