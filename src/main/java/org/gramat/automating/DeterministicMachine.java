package org.gramat.automating;

public class DeterministicMachine {

    public final Automaton am;
    public final State initial;
    public final StateSet accepted;

    public DeterministicMachine(Automaton am, State initial, StateSet accepted) {
        this.am = am;
        this.initial = initial;
        this.accepted = accepted;
    }
}
