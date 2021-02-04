package org.gramat.automating.transitions;

import org.gramat.automating.Automaton;
import org.gramat.automating.Level;
import org.gramat.automating.State;

public class TransitionEnter extends Transition {
    public final String name;
    public final Level level;
    public TransitionEnter(Automaton am, State source, State target, String name, Level level) {
        super(am, source, target);
        this.name = name;
        this.level = level;
    }

    @Override
    public Transition derive(State newSource, State newTarget) {
        return new TransitionEnter(am, newSource, newTarget, name, level);
    }
}
