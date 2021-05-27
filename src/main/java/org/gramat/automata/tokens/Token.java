package org.gramat.automata.tokens;

public interface Token {

    boolean isEmpty();

    boolean matches(Token token);

}
