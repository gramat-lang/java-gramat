package org.gramat.automating.engines;

import org.gramat.actions.ActionList;
import org.gramat.actions.HeapPop;
import org.gramat.actions.HeapPush;
import org.gramat.automating.Automaton;
import org.gramat.automating.Closure;
import org.gramat.automating.DeterministicMachine;
import org.gramat.automating.Direction;
import org.gramat.automating.Level;
import org.gramat.automating.Machine;
import org.gramat.automating.State;
import org.gramat.automating.transitions.Transition;
import org.gramat.automating.transitions.TransitionAction;
import org.gramat.automating.transitions.TransitionRecursion;
import org.gramat.automating.transitions.TransitionSymbol;
import org.gramat.codes.Code;
import org.gramat.exceptions.GramatException;
import org.gramat.logging.Logger;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class MergingEngine {

    public static DeterministicMachine resolve(Machine nMachine, Logger logger) {
        return new MergingEngine(logger).resolve(nMachine);
    }

    private final Logger logger;
    private final Map<String, Closure> idClosures;
    private final Map<String, State> idStates;
    private final Automaton dAuto;

    private MergingEngine(Logger logger) {
        this.logger = logger;
        this.idClosures = new LinkedHashMap<>();
        this.idStates = new LinkedHashMap<>();
        this.dAuto = new Automaton();
    }

    private DeterministicMachine resolve(Machine nMachine) {
        var nAuto = nMachine.am;
        var queue = new LinkedList<Closure>();
        var control = new HashSet<String>();
        var closure0 = ClosureEngine.empty(nMachine.begin, Direction.FORWARD);

        queue.add(closure0);

        var levels = nAuto.listLevels();
        var codes = nAuto.listCodes();

        logger.debug("Resolving %s codes and %s levels...", codes.size(), levels.size());

        do {
            var closure = queue.remove();
            if (control.add(closure.getID())) {
                var dSource = mapClosure(closure);

                for (var code : codes) {
                    for (var ts : findTransitionSymbols(closure, code)) {
                        var path = PathEngine.between(closure.getStates(), ts.source);
                        var targetClosure = ClosureEngine.empty(ts.target, Direction.FORWARD);
                        var dTarget = mapClosure(targetClosure);

                        var enterLevels = listLevels(path, Direction.FORWARD);
                        var exitLevels = listLevels(targetClosure.getTransitions(), Direction.BACKWARD);

                        var beginActions = listActions(path, Direction.FORWARD);
                        var endActions = listActions(targetClosure.getTransitions(), Direction.BACKWARD);

                        for (var level : enterLevels) {
                            beginActions.prepend(new HeapPush(level.id));
                        }

                        for (var level : exitLevels) {
                            endActions.append(new HeapPop(level.id));
                        }

                        dAuto.addMerged(dSource, dTarget, code, beginActions, endActions);

                        queue.add(targetClosure);
                    }
                }
            }
        }
        while (!queue.isEmpty());

        var begin = unmapClosure(closure0);
        var ends = unmapWith(nMachine.end);
        return new DeterministicMachine(dAuto, begin, ends);
    }

    private Set<Level> listLevels(Set<Transition> transitions, Direction dir) {
        var levels = new LinkedHashSet<Level>();

        for (var t : transitions) {
            if (t instanceof TransitionRecursion) {
                var tr = (TransitionRecursion) t;

                if (tr.direction == dir) {
                    levels.add(tr.level);
                }
            }
        }

        return levels;
    }

    private ActionList listActions(Set<Transition> transitions, Direction dir) {
        var levels = new ActionList();

        for (var t : transitions) {
            if (t instanceof TransitionAction) {
                var ta = (TransitionAction) t;

                if (ta.direction == dir) {
                    levels.append(ta.action);
                }
            }
        }

        return levels;
    }

    private boolean containsClosureID(String id) {
        return idStates.containsKey(id);
    }

    private State mapClosure(Closure closure) {
        return idStates.computeIfAbsent(closure.getID(), closureID -> {
            var state = dAuto.createState();

            idClosures.put(closureID, closure);

            return state;
        });
    }

    private Set<TransitionSymbol> findTransitionSymbols(Closure source, Code code) {
        var result = new LinkedHashSet<TransitionSymbol>();
        for (var state : source.getStates()) {
            source.am.walkForward(state, t -> {
                if (t instanceof TransitionSymbol) {
                    var ts = (TransitionSymbol) t;
                    if (ts.code == code) {
                        result.add(ts);
                    }
                    return false;
                }
                else {
                    // TODO check for unsupported transitions
                    return true;
                }
            });
        }
        return result;
    }

    private State unmapClosure(Closure closure) {
        var state = idStates.get(closure.getID());
        if (state == null) {
            throw new GramatException("state not found: " + closure.getID());
        }
        return state;
    }

    private Set<State> unmapWith(State end) {
        var result = new LinkedHashSet<State>();

        for (var closure : idClosures.values()) {
            if (closure.contains(end)) {
                var state = unmapClosure(closure);

                result.add(state);
            }
        }

        return result;
    }
}
