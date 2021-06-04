package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.evaluation.Context;
import org.gramat.automata.messages.KeyEndMessage;
import org.gramat.automata.messages.Message;

@Slf4j
public class KeyEnd extends Action {

    KeyEnd(int group) {
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

    @Override
    public Message createMessage(Context context) {
        return new KeyEndMessage(group);
    }

    @Override
    public String toString() {
        return String.format("key-end(%s)", group);
    }
}
