package org.gramat.automating.transitions;

import org.gramat.automating.Automaton;
import org.gramat.automating.State;
import org.gramat.codes.Code;

import java.util.Objects;

public class TransitionSymbol extends Transition {

    public final Code code;

    public TransitionSymbol(Automaton am, State source, State target, Code code) {
        super(am, source, target);
        this.code = Objects.requireNonNull(code);
    }

    @Override
    public Transition derive(State newSource, State newTarget) {
        return new TransitionSymbol(am, newSource, newTarget, code);
    }
}
