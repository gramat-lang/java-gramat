package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.evaluation.Context;
import org.gramat.automata.messages.Message;
import org.gramat.automata.messages.PutBeginMessage;

@Slf4j
public class PutBegin extends Action {

    PutBegin(int group) {
        super(group);
    }

    @Override
    public ActionType getType() {
        return ActionType.PUT;
    }

    @Override
    public ActionMode getMode() {
        return ActionMode.BEGIN;
    }

    @Override
    public Message createMessage(Context context) {
        return new PutBeginMessage(group);
    }

    @Override
    public String toString() {
        return String.format("put-begin(%s)", group);
    }
}
