package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.builder.DataBuilder;
import org.gramat.automata.tokens.Token;

import java.util.Objects;

@Slf4j
public class PushToken implements Action {

    private final Token token;

    PushToken(Token token) {
        this.token = Objects.requireNonNull(token);
    }

    @Override
    public String toString() {
        return "push(" + token + ")";
    }

    @Override
    public void run(DataBuilder builder) {
        builder.push(token);
    }

    public Token getToken() {
        return token;
    }
}
