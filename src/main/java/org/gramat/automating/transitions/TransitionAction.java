package org.gramat.automating.transitions;

import org.gramat.automating.ActionPlace;
import org.gramat.automating.Automaton;
import org.gramat.automating.Direction;
import org.gramat.automating.State;

public class TransitionAction extends Transition {
    public final ActionPlace action;
    public final Direction direction;
    public TransitionAction(Automaton am, State source, State target, ActionPlace action, Direction direction) {
        super(am, source, target);
        this.action = action;
        this.direction = direction;
    }

    @Override
    public Transition derive(State newSource, State newTarget) {
        return new TransitionAction(am, newSource, newTarget, action, direction);
    }
}
