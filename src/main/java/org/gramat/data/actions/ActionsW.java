package org.gramat.data.actions;

import org.gramat.actions.Action;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class ActionsW implements Actions {

    final LinkedList<Action> data;

    ActionsW() {
        data = new LinkedList<>();
    }

    ActionsW(Collection<Action> original) {
        data = new LinkedList<>(original);
    }

    public void prepend(Action action) {
        if (!data.contains(action)) {
            data.addFirst(action);
        }
    }

    public void prepend(ActionsW actions) {
        for (var action : actions.data) {
            prepend(action);
        }
    }

    public void append(Actions actions) {
        for (var action : actions) {
            append(action);
        }
    }

    public void append(Action action) {
        if (!data.contains(action)) {
            data.addLast(action);
        }
    }

    @Override
    public Iterator<Action> iterator() {
        return data.iterator();
    }

    @Override
    public boolean isPresent() {
        return !data.isEmpty();
    }
}
