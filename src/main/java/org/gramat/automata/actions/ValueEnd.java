package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.evaluation.Context;
import org.gramat.automata.messages.Message;
import org.gramat.automata.messages.ValueEndMessage;

@Slf4j
public class ValueEnd extends Action {
    public final String typeHint;

    ValueEnd(int group, String typeHint) {
        super(group);
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
    public String getTypeHint() {
        return typeHint;
    }

    @Override
    public boolean hasTypeHint() {
        return true;
    }

    @Override
    public Message createMessage(Context context) {
        return new ValueEndMessage(group, context.getPosition(), typeHint);
    }

    @Override
    public String toString() {
        if (typeHint != null) {
            return String.format("value-end(%s, %s)", group, typeHint);
        }
        return String.format("value-end(%s)", group);
    }
}
