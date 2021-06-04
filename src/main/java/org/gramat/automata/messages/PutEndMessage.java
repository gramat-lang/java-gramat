package org.gramat.automata.messages;

import org.gramat.automata.actions.ActionMode;
import org.gramat.automata.actions.ActionType;

public class PutEndMessage extends Message {
    private String keyHint;

    public PutEndMessage(int group, String keyHint) {
        super(group);
        this.keyHint = keyHint;
    }

    @Override
    public ActionType getType() {
        return ActionType.PUT;
    }

    @Override
    public ActionMode getMode() {
        return ActionMode.END;
    }

    @Override
    public String getKeyHint() {
        return keyHint;
    }

    @Override
    public void setKeyHint(String keyHint) {
        this.keyHint = keyHint;
    }
}
