package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
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
    public ActionType getType() {
        return ActionType.TOKEN;
    }

    @Override
    public ActionMode getMode() {
        return ActionMode.END;
    }

    @Override
    public Token getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "pop(" + token + ")";
    }
}
