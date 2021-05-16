package org.gramat.data;

import org.gramat.actions.Action;
import org.gramat.errors.ErrorFactory;

import java.util.Collection;
import java.util.List;


public interface Actions extends Iterable<Action> {

    static Actions empty() {
        return ActionsEmpty.INSTANCE;
    }

    static ActionsW createW() {
        return new ActionsW();
    }

    static ActionsW copyW(Actions original) {
        return new ActionsW(data(original));
    }

    static Actions join(Actions first, Actions last) {
        return new ActionsR(data(first), data(last));
    }

    static Actions join(Action first, Actions middle, Actions last) {
        return new ActionsR(List.of(first), data(middle), data(last));
    }

    static Actions join(Actions first, Actions middle, Action last) {
        return new ActionsR(data(first), data(middle), List.of(last));
    }

    private static Collection<Action> data(Actions actions) {
        if (actions == null || actions instanceof ActionsEmpty) {
            return List.of();
        }
        else if (actions instanceof ActionsW w) {
            return w.data;
        }
        else if (actions instanceof ActionsR r) {
            return r.data;
        }
        else {
            throw ErrorFactory.notImplemented();
        }
    }

}
