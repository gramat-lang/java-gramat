package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.evaluation.Context;
import org.gramat.automata.messages.KeyBeginMessage;
import org.gramat.automata.messages.Message;

@Slf4j
public class KeyBegin extends Action {

    KeyBegin(int group) {
        super(group);
    }

    @Override
    public ActionType getType() {
        return ActionType.KEY;
    }

    @Override
    public ActionMode getMode() {
        return ActionMode.BEGIN;
    }

    @Override
    public Message createMessage(Context context) {
        return new KeyBeginMessage(group);
    }

    @Override
    public String toString() {
        return String.format("key-begin(%s)", group);
    }

}
