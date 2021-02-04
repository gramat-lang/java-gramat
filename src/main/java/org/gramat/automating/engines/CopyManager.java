package org.gramat.automating.engines;

import org.gramat.automating.Automaton;
import org.gramat.automating.Machine;
import org.gramat.automating.State;
import org.gramat.automating.transitions.Transition;
import org.gramat.automating.transitions.TransitionBackward;
import org.gramat.automating.transitions.TransitionEmpty;
import org.gramat.automating.transitions.TransitionForward;
import org.gramat.automating.transitions.TransitionReference;
import org.gramat.automating.transitions.TransitionSymbol;
import org.gramat.exceptions.GramatException;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class CopyManager {

    private final Map<State, State> stateMap;
    private final Map<Transition, Transition> transitionMap;

    public final Automaton am;

    public CopyManager() {
        this(new Automaton());
    }

    public CopyManager(Automaton am) {
        this.am = am;
        this.stateMap = new LinkedHashMap<>();
        this.transitionMap = new LinkedHashMap<>();
    }

    public Machine copyMachine(Machine machine) {
        var queue = new ArrayDeque<State>();
        var control = new HashSet<State>();

        queue.add(machine.begin);
        queue.add(machine.end);

        while (!queue.isEmpty()) {
            var current = queue.remove();

            if (control.add(current)) {
                copyState(current);

                for (var t : machine.am.transitions) {
                    if (t.source == current) {
                        queue.add(t.target);

                        copyTransition(t);
                    } else if (t.target == current) {
                        queue.add(t.source);

                        copyTransition(t);
                    }
                }
            }
        }

        return findMachineCopy(machine.begin, machine.end);
    }

    public State copyState(State state) {
        return stateMap.computeIfAbsent(state, k -> this.am.createState(state.wild));
    }

    public Transition copyTransition(Transition transition) {
        var newSource = copyState(transition.source);
        var newTarget = copyState(transition.target);
        return transitionMap.computeIfAbsent(transition, k -> {
            if (transition instanceof TransitionEmpty) {
                return this.am.addEmpty(newSource, newTarget);
            }
            else if (transition instanceof TransitionSymbol) {
                var ts = (TransitionSymbol) transition;

                return this.am.addSymbol(newSource, newTarget, ts.code, ts.level, ts.beforeActions.copy(), ts.afterActions.copy());
            }
            else if (transition instanceof TransitionReference) {
                var tr = (TransitionReference) transition;

                return this.am.addReference(newSource, newTarget, tr.name, tr.level, tr.beforeActions.copy(), tr.afterActions.copy());
            }
            else if (transition instanceof TransitionForward) {
                var tf = (TransitionForward) transition;

                return this.am.addForward(newSource, newTarget, tf.action);
            }
            else if (transition instanceof TransitionBackward) {
                var tb = (TransitionBackward) transition;

                return this.am.addBackward(newSource, newTarget, tb.action);
            }
            else {
                throw new GramatException("not supported transition: " + transition);
            }
        });
    }

    public Machine findMachineCopy(State begin, State end) {
        return new Machine(
                this.am,
                findStateCopy(begin),
                findStateCopy(end));
    }

    public State findStateCopy(State original) {
        var copy = stateMap.get(original);
        if (copy == null) {
            throw new GramatException("copy not found");
        }
        return copy;
    }
}
