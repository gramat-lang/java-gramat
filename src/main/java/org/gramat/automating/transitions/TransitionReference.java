package org.gramat.automating.transitions;

import org.gramat.actions.ActionList;
import org.gramat.automating.Automaton;
import org.gramat.automating.Level;
import org.gramat.automating.State;

public class TransitionReference extends TransitionWrapper {

    public final String name;
    public final Level level;

    public TransitionReference(Automaton am, State source, State target, String name, Level level, ActionList beforeActions, ActionList afterActions) {
        super(am, source, target, beforeActions, afterActions);
        this.name = name;
        this.level = level;
    }

    @Override
    public Transition derive(State newSource, State newTarget) {
        return new TransitionReference(am, newSource, newTarget, name, level, beforeActions.copy(), afterActions.copy());
    }
}
