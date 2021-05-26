package org.gramat.actions;

import java.util.Collections;
import java.util.Iterator;

class Actions0 implements Actions {

    public static final Actions0 INSTANCE = new Actions0();

    private Actions0() {}

    @Override
    public Iterator<Action> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public boolean isPresent() {
        return false;
    }
}
