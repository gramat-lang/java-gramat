package org.gramat.automata.tokens;

import java.util.Objects;

public class TokenString implements Token {

    private final String key;

    public TokenString(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean matches(Token token) {
        if (token instanceof TokenString ts) {
            return Objects.equals(this.key, ts.key);
        }
        return false;
    }

    @Override
    public String toString() {
        return key;
    }
}
