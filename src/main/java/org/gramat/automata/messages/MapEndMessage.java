package org.gramat.automata.messages;

import org.gramat.automata.actions.ActionMode;
import org.gramat.automata.actions.ActionType;

public class MapEndMessage extends Message {
    private String typeHint;

    public MapEndMessage(int group, String typeHint) {
        super(group);
        this.typeHint = typeHint;
    }

    @Override
    public ActionType getType() {
        return ActionType.MAP;
    }

    @Override
    public ActionMode getMode() {
        return ActionMode.END;
    }

    @Override
    public void setTypeHint(String typeHint) {
        this.typeHint = typeHint;
    }

    @Override
    public String getTypeHint() {
        return typeHint;
    }
}
