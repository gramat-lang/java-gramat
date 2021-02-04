package org.gramat.automating;

import java.util.Set;

public class DeterministicMachine {

    public final Automaton am;
    public final State initial;
    public final Set<State> accepted;

    public DeterministicMachine(Automaton am, State initial, Set<State> accepted) {
        this.am = am;
        this.initial = initial;
        this.accepted = accepted;
    }
}
