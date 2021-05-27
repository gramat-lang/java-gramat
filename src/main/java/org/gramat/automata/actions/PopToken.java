package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.evaluation.Context;
import org.gramat.automata.tapes.Tape;
import org.gramat.automata.tokens.Token;

import java.util.Objects;

@Slf4j
public class PopToken extends Action {
    public final Token token;

    PopToken(int group, Token token) {
        super(group);
        this.token = Objects.requireNonNull(token);
    }

    @Override
    public void run(Tape tape, Context context) {
        log.debug("RUN {}", this);

        context.pop(token);
    }

    @Override
    public String toString() {
        return "pop(" + token + ")";
    }
}
