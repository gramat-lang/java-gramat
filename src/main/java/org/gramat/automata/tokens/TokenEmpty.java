package org.gramat.automata.tokens;

public class TokenEmpty implements Token {

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean matches(Token token) {
        return true;
    }
}
