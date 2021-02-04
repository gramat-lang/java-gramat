package org.gramat.automating.transitions;

import org.gramat.actions.ActionList;
import org.gramat.automating.Automaton;
import org.gramat.automating.Level;
import org.gramat.automating.State;
import org.gramat.codes.Code;

import java.util.Objects;

public class TransitionSymbol extends TransitionWrapper {
    // TODO create simple version and pushdown version
    
    public final Code code;
    public final Level level;

    public TransitionSymbol(Automaton am, State source, State target, Code code, Level level, ActionList beforeActions, ActionList afterActions) {
        super(am, source, target, beforeActions, afterActions);
        this.code = Objects.requireNonNull(code);
        this.level = Objects.requireNonNull(level);
    }

    @Override
    public Transition derive(State newSource, State newTarget) {
        return new TransitionSymbol(am, newSource, newTarget, code, level, beforeActions.copy(), afterActions.copy());
    }
}
