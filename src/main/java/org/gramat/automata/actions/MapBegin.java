package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.evaluation.Context;
import org.gramat.automata.messages.MapBeginMessage;
import org.gramat.automata.messages.Message;

@Slf4j
public class MapBegin extends Action {

    MapBegin(int group) {
        super(group);
    }

    @Override
    public ActionType getType() {
        return ActionType.MAP;
    }

    @Override
    public ActionMode getMode() {
        return ActionMode.BEGIN;
    }

    @Override
    public Message createMessage(Context context) {
        return new MapBeginMessage(group);
    }

    @Override
    public String toString() {
        return String.format("map-begin(%s)", group);
    }
}
