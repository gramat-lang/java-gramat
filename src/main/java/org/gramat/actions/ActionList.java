package org.gramat.actions;

import org.gramat.util.PP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ActionList implements Iterable<Action> {

    private List<Action> actions;

    private static List<Action> join(Iterable<Action> pre, Iterable<Action> post) {
        var actions = new ArrayList<Action>();

        if (pre != null) {
            for (var a : pre) {
                if (!actions.contains(a)) {
                    actions.add(a);
                }
            }
        }

        if (post != null) {
            for (var a : post) {
                if (!actions.contains(a)) {
                    actions.add(a);
                }
            }
        }

        return actions;
    }

    public void prepend(Action action) {
        actions = join(List.of(action), actions);
    }

    public void prependAll(Iterable<Action> i) {
        actions = join(i, actions);
    }

    public void append(Action action) {
        actions = join(actions, List.of(action));
    }

    public void appendAll(Iterable<Action> i) {
        actions = join(actions, i);
    }

    @Override
    public Iterator<Action> iterator() {
        return actions != null ? actions.iterator() : Collections.emptyIterator();
    }

    public boolean isPresent() {
        return actions != null && !actions.isEmpty();
    }

    public boolean isEmpty() {
        return actions == null || actions.isEmpty();
    }

    @Override
    public String toString() {
        return PP.str(actions);
    }

    public ActionList copy() {
        var that = new ActionList();
        if (this.actions != null) {
            that.actions = new ArrayList<>(this.actions);
        }
        return that;
    }
}
