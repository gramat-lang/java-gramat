package org.gramat.automating.engines;

import org.gramat.actions.Action;
import org.gramat.actions.design.ActionMaker;
import org.gramat.automating.DeterministicMachine;
import org.gramat.automating.Direction;
import org.gramat.automating.State;
import org.gramat.automating.transitions.TransitionAction;
import org.gramat.automating.transitions.TransitionMerged;
import org.gramat.codes.Code;
import org.gramat.eval.EvalLink;
import org.gramat.eval.EvalNode;
import org.gramat.eval.EvalProgram;
import org.gramat.logging.Logger;
import org.gramat.tracking.SourceMap;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EvalNodeEngine {

    public static EvalProgram run(DeterministicMachine machine, Logger logger) {
        var engine = new EvalNodeEngine(logger);

        return engine.run(machine);
    }

    private final Logger logger;
    private final Map<State, EvalNode> stateNodes;
    private final SourceMap sourceMap;

    private int nextID;

    private EvalNodeEngine(Logger logger) {
        this.logger = logger;
        this.stateNodes = new LinkedHashMap<>();
        this.sourceMap = new SourceMap();
    }

    private EvalProgram run(DeterministicMachine machine) {
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
                        var beginActions = new ArrayList<Action>();
                        var endActions = new ArrayList<Action>();

                        for (var template : tm.beginActions) {
                            var action = ActionMaker.make(template);

                            beginActions.add(action);

                            sourceMap.addActionLocations(action.id, template.locations);
                        }

                        for (var template : tm.endActions) {
                            var action = ActionMaker.make(template);

                            endActions.add(action);

                            sourceMap.addActionLocations(action.id, template.locations);
                        }

                        var link = new EvalLink(
                                tm.code,
                                targetNode,
                                compileActions(beginActions),
                                compileActions(endActions));

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
        var main = stateNodes.get(machine.initial);
        return new EvalProgram(main, sourceMap);
    }

    private Action[] compileActions(List<Action> actions) {
        if (actions.isEmpty()) {
            return null;
        }
        if (actions.size() > 3) {
            actions.toArray();
        }
        return actions.toArray(Action[]::new);
    }

    private EvalNode verifyMapping(State state) {
        return stateNodes.computeIfAbsent(state, k -> {
           nextID++;
           var id = nextID;

           sourceMap.addNodeLocations(id, state.locations);

           return new EvalNode(id);
        });
    }


}
