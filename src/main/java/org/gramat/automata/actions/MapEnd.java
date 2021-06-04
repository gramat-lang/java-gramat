package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.evaluation.Context;
import org.gramat.automata.messages.MapEndMessage;
import org.gramat.automata.messages.Message;

@Slf4j
public class MapEnd extends Action {

    private final String typeHint;

    MapEnd(int group, String typeHint) {
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
    public boolean hasTypeHint() {
        return true;
    }

    @Override
    public String getTypeHint() {
        return typeHint;
    }

    @Override
    public Message createMessage(Context context) {
        return new MapEndMessage(group, typeHint);
    }

    @Override
    public String toString() {
        if (typeHint != null) {
            return String.format("map-end(%s, %s)", group, typeHint);
        }
        return String.format("map-end(%s)", group);
    }
}
