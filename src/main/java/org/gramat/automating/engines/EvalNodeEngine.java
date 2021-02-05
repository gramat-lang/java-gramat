package org.gramat.automating.engines;

import org.gramat.automating.DeterministicMachine;
import org.gramat.automating.Direction;
import org.gramat.automating.State;
import org.gramat.automating.transitions.TransitionAction;
import org.gramat.automating.transitions.TransitionMerged;
import org.gramat.codes.Code;
import org.gramat.eval.EvalLink;
import org.gramat.eval.EvalNode;
import org.gramat.logging.Logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class EvalNodeEngine {

    public static EvalNode run(DeterministicMachine machine, Logger logger) {
        var engine = new EvalNodeEngine(logger);

        return engine.run(machine);
    }

    private final Logger logger;
    private final Map<State, EvalNode> stateNodes;

    private int nextID;

    private EvalNodeEngine(Logger logger) {
        this.logger = logger;
        this.stateNodes = new LinkedHashMap<>();
    }

    private EvalNode run(DeterministicMachine machine) {
        var queue = new ArrayDeque<State>();
        var control = new HashSet<State>();

        queue.add(machine.initial);

        do {
            var state = queue.remove();
            if (control.add(state)) {
                var sourceNode = verifyMapping(state);
                var links = new ArrayList<EvalLink>();

                for (var t : state.am.findTransitions(state, Direction.FORWARD)) {
                    if (t instanceof TransitionMerged) {
                        var tm = (TransitionMerged)t;
                        var targetNode = verifyMapping(tm.target);

                        var link = new EvalLink(
                                tm.code,
                                targetNode,
                                tm.beginActions.toArray(),
                                tm.endActions.toArray());

                        links.add(link);

                        queue.add(tm.target);
                    }
                    else {
                        throw new RuntimeException("unsupported transition: " + t);
                    }
                }

                sourceNode.links = links.toArray(EvalLink[]::new);
            }
        } while (!queue.isEmpty());

        // mark as accepted
        for (var accepted : machine.accepted) {
            stateNodes.get(accepted).accepted = true;
        }

        // return initial node
        return stateNodes.get(machine.initial);
    }

    private EvalNode verifyMapping(State state) {
        return stateNodes.computeIfAbsent(state, k -> {
           nextID++;
           return new EvalNode(nextID);
        });
    }


}
