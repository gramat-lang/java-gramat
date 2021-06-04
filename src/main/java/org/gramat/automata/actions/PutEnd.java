package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.evaluation.Context;
import org.gramat.automata.messages.Message;
import org.gramat.automata.messages.PutEndMessage;

@Slf4j
public class PutEnd extends Action {
    public final String keyHint;

    PutEnd(int group, String keyHint) {
        super(group);
        this.keyHint = keyHint;
    }

    @Override
    public ActionType getType() {
        return ActionType.PUT;
    }

    @Override
    public ActionMode getMode() {
        return ActionMode.END;
    }

    @Override
    public String getKeyHint() {
        return keyHint;
    }

    @Override
    public boolean hasKeyHint() {
        return true;
    }

    @Override
    public Message createMessage(Context context) {
        return new PutEndMessage(group, keyHint);
    }

    @Override
    public String toString() {
        if (keyHint != null) {
            return String.format("put-end(%s, %s)", group, keyHint);
        }
        return String.format("put-end(%s)", group);
    }
}
