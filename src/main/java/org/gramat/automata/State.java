package org.gramat.automata;

import org.gramat.automata.tokens.Token;

public class State {

    private static final Transition[] EMPTY = new Transition[0];

    private final boolean accepted;

    private Transition[] transitions;

    State(boolean accepted) {
        this.accepted = accepted;
        this.transitions = EMPTY;
    }

    public Transition findTransitionFor(char symbol, Token token) {
        for (var transition : transitions) {
            if (transition.getToken().matches(token) && transition.getSymbol().matches(symbol)) {
                return transition;
            }
        }

        return null;
    }

    public Transition[] getTransitions() {
        return transitions;
    }

    public void setTransitions(Transition[] transitions) {
        if (transitions == null || transitions.length == 0) {
            this.transitions = EMPTY;
        }
        else {
            this.transitions = transitions;
        }
    }

    public boolean isAccepted() {
        return accepted;
    }
}
