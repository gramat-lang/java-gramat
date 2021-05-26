package org.gramat.actions;

import org.gramat.tools.DataUtils;

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
        return DataUtils.immutableIterator(data);
    }

    @Override
    public boolean isPresent() {
        return !data.isEmpty();
    }
}
