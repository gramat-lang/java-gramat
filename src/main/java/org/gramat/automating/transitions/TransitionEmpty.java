package org.gramat.automating.transitions;

import org.gramat.automating.Automaton;
import org.gramat.automating.State;

public class TransitionEmpty extends Transition {
    public TransitionEmpty(Automaton am, State source, State target) {
        super(am, source, target);
    }

    @Override
    public Transition derive(State newSource, State newTarget) {
        return new TransitionEmpty(am, newSource, newTarget);
    }
}
