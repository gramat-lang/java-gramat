package org.gramat.automating;

import org.gramat.automating.transitions.Transition;
import org.gramat.util.Lazy;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public class Closure {

    public final Automaton am;
    private final Set<Transition> transitions;
    private final Set<State> states;
    private final Lazy<String> lazyID;

    public Closure(Automaton am, Set<Transition> transitions, Set<State> states) {
        this.am = am;
        this.transitions = transitions;
        this.states = states;
        this.lazyID = Lazy.of(() -> states.stream()
                .sorted(Comparator.comparingInt(a -> a.id))
                .map(a -> String.valueOf(a.id))
                .collect(Collectors.joining("_")));
    }

    public Set<State> getStates() {
        return Collections.unmodifiableSet(states);
    }

    public Set<Transition> getTransitions() {
        return Collections.unmodifiableSet(transitions);
    }

    public String getID() {
        return lazyID.get();
    }

    public boolean isPresent() {
        return !states.isEmpty();
    }

    public boolean contains(State state) {
        return states.contains(state);
    }
}
