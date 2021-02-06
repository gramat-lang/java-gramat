package org.gramat.automating;

import org.gramat.automating.transitions.Transition;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Machine {

    public final Automaton am;
    public final State begin;
    public final State end;

    public Machine(Automaton am, State begin, State end) {
        this.am = Objects.requireNonNull(am);
        this.begin = Objects.requireNonNull(begin);
        this.end = Objects.requireNonNull(end);

        if (begin.am != am || end.am != am) {
            throw new RuntimeException();
        }
    }

    public Set<Transition> findTransitions() {
        var result = new LinkedHashSet<Transition>();
        var control = new HashSet<State>();
        var queue = new ArrayDeque<State>();

        queue.add(begin);
        queue.add(end);

        while (!queue.isEmpty()) {
            var state = queue.remove();
            if (control.add(state)) {
                for (var t : am.transitions) {
                    if (t.source == state) {
                        result.add(t);

                        queue.add(t.target);
                    }
                    else if (t.target == state) {
                        result.add(t);

                        queue.add(t.source);
                    }
                }
            }
        }

        return result;
    }

}
