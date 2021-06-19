package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.builder.DataBuilder;
import org.gramat.automata.tokens.Token;

import java.util.Objects;

@Slf4j
public class PopToken implements Action {
    private final Token token;

    PopToken(Token token) {
        this.token = Objects.requireNonNull(token);
    }

    @Override
    public String toString() {
        return "pop(" + token + ")";
    }

    @Override
    public void run(DataBuilder builder) {
        builder.pop(token);
    }

    public Token getToken() {
        return token;
    }
}
