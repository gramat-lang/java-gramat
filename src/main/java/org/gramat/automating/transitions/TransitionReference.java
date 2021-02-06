package org.gramat.automating.transitions;

import org.gramat.actions.design.ActionTemplate;
import org.gramat.automating.Automaton;
import org.gramat.automating.Level;
import org.gramat.automating.State;

public class TransitionReference extends Transition {

    public final String name;
    public final Level level;
    public final ActionTemplate enterAction;
    public final ActionTemplate exitAction;

    public TransitionReference(Automaton am, State source, State target, String name, Level level, ActionTemplate enterAction, ActionTemplate exitAction) {
        super(am, source, target);
        this.name = name;
        this.level = level;
        this.enterAction = enterAction;
        this.exitAction = exitAction;
    }

    @Override
    public Transition derive(State newSource, State newTarget) {
        return new TransitionReference(am, newSource, newTarget, name, level, enterAction, exitAction);
    }
}
