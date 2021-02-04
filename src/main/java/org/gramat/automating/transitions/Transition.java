package org.gramat.automating.transitions;

import org.gramat.automating.Automaton;
import org.gramat.automating.State;

public abstract class Transition {
    public abstract Transition derive(State newSource, State newTarget);

    public final Automaton am;
    public final State source;
    public final State target;

    Transition(Automaton am, State source, State target) {
        this.am = am;
        this.source = source;
        this.target = target;
        // TODO validate same automaton
    }
}
