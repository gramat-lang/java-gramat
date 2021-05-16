package org.gramat.data;

import org.gramat.actions.Action;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;


public class ActionsR implements Actions {

    final LinkedHashSet<Action> data;

    ActionsR(Collection<Action> first, Collection<Action> last) {
        data = new LinkedHashSet<>(first);
        data.addAll(last);
    }

    ActionsR(Collection<Action> first, Collection<Action> middle, Collection<Action> last) {
        data = new LinkedHashSet<>(first);
        data.addAll(middle);
        data.addAll(last);
    }

    @Override
    public Iterator<Action> iterator() {
        var iterator = data.iterator();
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Action next() {
                return iterator.next();
            }
        };
    }
}
