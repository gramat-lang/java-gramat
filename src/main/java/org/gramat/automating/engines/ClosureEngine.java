package org.gramat.automating.engines;

import org.gramat.automating.Automaton;
import org.gramat.automating.Branch;
import org.gramat.automating.Closure;
import org.gramat.automating.Direction;
import org.gramat.automating.Level;
import org.gramat.automating.State;
import org.gramat.automating.transitions.Transition;
import org.gramat.automating.transitions.TransitionAction;
import org.gramat.automating.transitions.TransitionEmpty;
import org.gramat.automating.transitions.TransitionRecursion;
import org.gramat.automating.transitions.TransitionSymbol;
import org.gramat.codes.Code;
import org.gramat.exceptions.GramatException;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ClosureEngine {

    public static Closure empty(State base, Direction dir) {
        var states = new LinkedHashSet<State>();
        var transitions = new LinkedHashSet<Transition>();
        var am = base.am;

        emptyClosure(am, base, dir, states, transitions);

        return new Closure(am, transitions, states);
    }

    private ClosureEngine() {}

    private static void emptyClosure(Automaton am, State base, Direction dir, Set<State> states, Set<Transition> transitions) {
        var queue = new ArrayDeque<State>();
        var control = new HashSet<State>();

        queue.add(base);

        do {
            var state = queue.remove();

            if (control.add(state)) {
                states.add(state);

                for (var t : am.findTransitions(state, dir)) {
                    if (t instanceof TransitionEmpty || t instanceof TransitionAction) {
                        transitions.add(t);

                        queue.add(t.target);
                    }
                    else if (!(t instanceof TransitionSymbol || t instanceof TransitionRecursion)) {
                        throw new GramatException("unsupported transition: " + t);
                    }
                }
            }
        } while (!queue.isEmpty());
    }

}
