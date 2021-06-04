package org.gramat.automata.messages;

import org.gramat.automata.actions.ActionMode;
import org.gramat.automata.actions.ActionType;

public class KeyEndMessage extends Message {

    public KeyEndMessage(int group) {
        super(group);
    }

    @Override
    public ActionType getType() {
        return ActionType.KEY;
    }

    @Override
    public ActionMode getMode() {
        return ActionMode.END;
    }
}
