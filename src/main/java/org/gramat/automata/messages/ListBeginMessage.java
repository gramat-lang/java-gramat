package org.gramat.automata.messages;

import org.gramat.automata.actions.ActionMode;
import org.gramat.automata.actions.ActionType;

public class ListBeginMessage extends Message {

    public ListBeginMessage(int group) {
        super(group);
    }

    @Override
    public ActionType getType() {
        return ActionType.LIST;
    }

    @Override
    public ActionMode getMode() {
        return ActionMode.BEGIN;
    }


}
