package org.gramat.automating.transitions;

import org.gramat.actions.ActionList;
import org.gramat.automating.Automaton;
import org.gramat.automating.State;

import java.util.Objects;

public abstract class TransitionWrapper extends Transition {

    public final ActionList beforeActions;
    public final ActionList afterActions;

    TransitionWrapper(Automaton am, State source, State target, ActionList beforeActions, ActionList afterActions) {
        super(am, source, target);
        this.beforeActions = Objects.requireNonNull(beforeActions);
        this.afterActions = Objects.requireNonNull(afterActions);
    }
}
