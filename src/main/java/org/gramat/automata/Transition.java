package org.gramat.automata;

public interface Transition {

    State getTarget(Symbol symbol, Token token);

}
