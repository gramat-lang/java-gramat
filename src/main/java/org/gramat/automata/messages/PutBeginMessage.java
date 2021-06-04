package org.gramat.automata.messages;

import org.gramat.automata.actions.ActionMode;
import org.gramat.automata.actions.ActionType;

public class PutBeginMessage extends Message {
    public PutBeginMessage(int group) {
        super(group);
    }

    @Override
    public ActionType getType() {
        return ActionType.PUT;
    }

    @Override
    public ActionMode getMode() {
        return ActionMode.BEGIN;
    }
}
