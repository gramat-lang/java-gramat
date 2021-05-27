package org.gramat.automata.tokens;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TokenFactory {

    private final List<Token> tokens;

    public TokenFactory() {
        tokens = new ArrayList<>();
    }

    public Token token(String key) {
        Objects.requireNonNull(key);

        for (var token : tokens) {
            if (token instanceof TokenString ts && key.equals(ts.getKey())) {
                return ts;
            }
        }

        var ts = new TokenString(key);
        tokens.add(ts);
        return ts;
    }

    public Token empty() {
        for (var token : tokens) {
            if (token.isEmpty()) {
                return token;
            }
        }

        var ts = new TokenEmpty();
        tokens.add(ts);
        return ts;
    }

    public Token[] toArray() {
        return tokens.toArray(new Token[0]);
    }
}
