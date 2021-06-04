package org.gramat.automata.actions;

import org.gramat.automata.evaluation.Context;
import org.gramat.automata.messages.Message;
import org.gramat.automata.tokens.Token;

public abstract class Action {

    public abstract ActionType getType();

    public abstract ActionMode getMode();

    protected final int group;

    Action(int group) {
        this.group = group;
    }

    public final int getGroup() {
        return group;
    }

    public Message createMessage(Context context) {
        throw new IllegalStateException();
    }

    public Token getToken() {
        throw new IllegalStateException();
    }

    public boolean hasKeyHint() {
        return false;
    }

    public void setKeyHint(String typeHint) {
        throw new IllegalStateException();
    }

    public String getKeyHint() {
        throw new IllegalStateException();
    }

    public boolean hasTypeHint() {
        return false;
    }

    public void setTypeHint(String typeHint) {
        throw new IllegalStateException();
    }

    public String getTypeHint() {
        throw new IllegalStateException();
    }
}
