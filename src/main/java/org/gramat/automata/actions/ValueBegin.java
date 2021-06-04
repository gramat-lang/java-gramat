package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.evaluation.Context;
import org.gramat.automata.messages.Message;
import org.gramat.automata.messages.ValueBeginMessage;

@Slf4j
public class ValueBegin extends Action {

    ValueBegin(int group) {
        super(group);
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
    public Message createMessage(Context context) {
        return new ValueBeginMessage(group, context.getPosition());
    }

    @Override
    public String toString() {
        return String.format("value-begin(%s)", group);
    }
}
