package org.gramat.automata.messages;

import org.gramat.automata.actions.ActionMode;
import org.gramat.automata.actions.ActionType;

public class ValueEndMessage extends Message {
    private String typeHint;
    private int position;

    public ValueEndMessage(int group, int position, String typeHint) {
        super(group);
        this.position = position;
        this.typeHint = typeHint;
    }

    @Override
    public ActionType getType() {
        return ActionType.VALUE;
    }

    @Override
    public ActionMode getMode() {
        return ActionMode.END;
    }

    @Override
    public boolean hasPosition() {
        return true;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public String getTypeHint() {
        return typeHint;
    }

    @Override
    public void setTypeHint(String typeHint) {
        this.typeHint = typeHint;
    }
}
