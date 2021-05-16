package org.gramat.data;

import org.gramat.actions.Action;

import java.util.Collections;
import java.util.Iterator;

class ActionsEmpty implements Actions {

    public static final ActionsEmpty INSTANCE = new ActionsEmpty();

    private ActionsEmpty() {}

    @Override
    public Iterator<Action> iterator() {
        return Collections.emptyIterator();
    }
}
