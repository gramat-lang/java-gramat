package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.evaluation.Context;
import org.gramat.automata.messages.ListEndMessage;
import org.gramat.automata.messages.Message;

@Slf4j
public class ListEnd extends Action {

    public final String typeHint;

    ListEnd(int group, String typeHint) {
        super(group);
        this.typeHint = typeHint;
    }

    @Override
    public ActionType getType() {
        return ActionType.LIST;
    }

    @Override
    public ActionMode getMode() {
        return ActionMode.END;
    }

    @Override
    public Message createMessage(Context context) {
        return new ListEndMessage(group, typeHint);
    }

    @Override
    public String getTypeHint() {
        return typeHint;
    }

    @Override
    public boolean hasTypeHint() {
        return true;
    }

    @Override
    public String toString() {
        if (typeHint != null) {
            return String.format("list-end(%s, %s)", group, typeHint);
        }
        return String.format("list-end(%s)", group);
    }
}
