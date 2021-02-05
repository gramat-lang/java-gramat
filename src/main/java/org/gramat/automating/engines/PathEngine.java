package org.gramat.automating.engines;

import org.gramat.automating.Closure;
import org.gramat.automating.Direction;
import org.gramat.automating.State;
import org.gramat.automating.transitions.Transition;
import org.gramat.automating.transitions.TransitionAction;
import org.gramat.automating.transitions.TransitionEmpty;
import org.gramat.automating.transitions.TransitionRecursion;
import org.gramat.automating.transitions.TransitionSymbol;
import org.gramat.exceptions.GramatException;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class PathEngine {
    public static Set<Transition> between(Set<State> sources, State target) {
        var path = new LinkedHashSet<Transition>();
        var queue = new ArrayDeque<>(sources);
        var control = new HashSet<State>();

        while (!queue.isEmpty()) {
            var source = queue.remove();
            if (control.add(source)) {
                for (var t : source.am.findTransitions(source, Direction.FORWARD)) {
                    if (goesTo(t, target)) {
                        path.add(t);
                        queue.add(t.target);
                    }
                }
            }
        }

        return path;
    }

    private static boolean goesTo(Transition transition, State target) {
        return goesTo(transition, target, new HashSet<>());
    }

    private static boolean goesTo(Transition transition, State target, Set<State> control) {
        if (transition instanceof TransitionSymbol) {
            return false;
        }
        else if (transition instanceof TransitionEmpty
                || transition instanceof TransitionRecursion
                || transition instanceof TransitionAction) {
            if (transition.target == target) {
                return true;
            }

            if (control.add(transition.target)) {
                for (var t : transition.am.findTransitions(transition.target, Direction.FORWARD)) {
                    if (goesTo(t, target, control)) {
                        return true;
                    }
                }
            }

            return false;
        }
        else {
            throw new GramatException("unsupported");
        }
    }
}
