package org.gramat.automating.engines;

import org.gramat.actions.ActionList;
import org.gramat.automating.Automaton;
import org.gramat.automating.DeterministicMachine;
import org.gramat.automating.Level;
import org.gramat.automating.Machine;
import org.gramat.automating.State;
import org.gramat.automating.StateSet;
import org.gramat.exceptions.GramatException;
import org.gramat.logging.Logger;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class DeterministicEngine {

    public static DeterministicMachine resolve(Machine nMachine, Logger logger) {
        return new DeterministicEngine(logger).resolve(nMachine);
    }

    private final Logger logger;
    private final Map<String, StateSet> idClosures;
    private final Map<String, State> idStates;
    private final Automaton dAuto;

    private DeterministicEngine(Logger logger) {
        this.logger = logger;
        this.idClosures = new LinkedHashMap<>();
        this.idStates = new LinkedHashMap<>();
        this.dAuto = new Automaton();
    }

    private DeterministicMachine resolve(Machine nMachine) {
        var nAuto = nMachine.am;
        var queue = new LinkedList<StateSet>();
        var control = new HashSet<String>();
        var closure0 = nAuto.emptyClosure(nMachine.begin);

        queue.add(closure0);

        logger.debug("Resolving %s codes and %s levels...", nAuto.codes.size(), nAuto.levels.size());

        do {
            var closure = queue.remove();
            if (control.add(closure.getID())) {
                var dSource = mapClosure(closure);

                for (var code : nAuto.codes) {
//                    for (var level : nam.levels) {
                        var targets = StateSet.of();
                        var beforeActions = new ActionList();
                        var afterActions = new ActionList();

                        for (var t : nAuto.transitionsFrom(closure, code, Level.ANY)) {
                            beforeActions.prependAll(t.beforeActions);
                            afterActions.appendAll(t.afterActions);

                            targets.add(t.target);
                        }

                        if (targets.isPresent()) {
                            var targetClosure = nAuto.emptyClosure(targets);
                            var dTarget = mapClosure(targetClosure);

                            dAuto.addSymbol(dSource, dTarget, code, Level.ANY, beforeActions, afterActions);

                            queue.add(targetClosure);
                        }
//                    }
                }
            }
        }
        while (!queue.isEmpty());

        var begin = unmapClosure(closure0);
        var ends = unmapWith(nMachine.end);
        return new DeterministicMachine(dAuto, begin, ends);
    }

    private boolean containsClosureID(String id) {
        return idStates.containsKey(id);
    }

    private State mapClosure(StateSet closure) {
        return idStates.computeIfAbsent(closure.getID(), closureID -> {
            var state = dAuto.createState();

            idClosures.put(closureID, closure);

            return state;
        });
    }

    private State unmapClosure(StateSet closure) {
        var state = idStates.get(closure.getID());
        if (state == null) {
            throw new GramatException("state not found: " + closure.getID());
        }
        return state;
    }

    private StateSet unmapWith(State end) {
        var result = StateSet.of();

        for (var closure : idClosures.values()) {
            if (closure.contains(end)) {
                var state = unmapClosure(closure);

                result.add(state);
            }
        }

        return result;
    }
}
