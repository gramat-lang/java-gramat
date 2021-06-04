package org.gramat.automata.messages;

import org.gramat.automata.actions.ActionMode;
import org.gramat.automata.actions.ActionType;

public class MapBeginMessage extends Message {
    public MapBeginMessage(int group) {
        super(group);
    }

    @Override
    public ActionType getType() {
        return ActionType.MAP;
    }

    @Override
    public ActionMode getMode() {
        return ActionMode.BEGIN;
    }
}
