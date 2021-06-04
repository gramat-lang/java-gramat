package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.evaluation.Context;
import org.gramat.automata.messages.ListBeginMessage;
import org.gramat.automata.messages.Message;

@Slf4j
public class ListBegin extends Action {

    ListBegin(int group) {
        super(group);
    }

    @Override
    public ActionType getType() {
        return ActionType.LIST;
    }

    @Override
    public ActionMode getMode() {
        return ActionMode.BEGIN;
    }

    @Override
    public Message createMessage(Context context) {
        return new ListBeginMessage(group);
    }

    @Override
    public String toString() {
        return String.format("list-begin(%s)", group);
    }
}
