package org.gramat.automata.messages;

import org.gramat.automata.actions.ActionMode;
import org.gramat.automata.actions.ActionType;

public class ValueBeginMessage extends Message {

    private int position;

    public ValueBeginMessage(int group, int position) {
        super(group);
        this.position = position;
    }

    @Override
    public ActionType getType() {
        return ActionType.VALUE;
    }

    @Override
    public ActionMode getMode() {
        return ActionMode.BEGIN;
    }

    @Override
    public boolean hasPosition() {
        return true;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }
}
