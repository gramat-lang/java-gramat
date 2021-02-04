package org.gramat.automating.transitions;

import org.gramat.actions.Action;
import org.gramat.automating.Automaton;
import org.gramat.automating.State;

public class TransitionForward extends Transition {
    public final Action action;
    public TransitionForward(Automaton am, State source, State target, Action action) {
        super(am, source, target);
        this.action = action;
    }

    @Override
    public Transition derive(State newSource, State newTarget) {
        return new TransitionForward(am, newSource, newTarget, action);
    }
}
