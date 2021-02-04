package org.gramat.automating;

public class State {

    public final Automaton am;
    public final int id;
    public final boolean wild;

    public State(Automaton am, int id, boolean wild) {
        this.am = am;
        this.id = id;
        this.wild = wild;
    }

}
