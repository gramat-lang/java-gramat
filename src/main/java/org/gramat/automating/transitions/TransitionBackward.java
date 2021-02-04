package org.gramat.automating.transitions;

import org.gramat.actions.Action;
import org.gramat.automating.Automaton;
import org.gramat.automating.State;

public class TransitionBackward extends Transition {
    public final Action action;
    public TransitionBackward(Automaton am, State source, State target, Action action) {
        super(am, source, target);
        this.action = action;
    }

    @Override
    public Transition derive(State newSource, State newTarget) {
        return new TransitionBackward(am, newSource, newTarget, action);
    }
}
