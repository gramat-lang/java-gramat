package org.gramat.automata.actions;

import org.gramat.automata.evaluation.Context;
import org.gramat.automata.tapes.Tape;

public abstract class Action {

    public abstract void run(Tape tape, Context context);

    protected final int group;

    Action(int group) {
        this.group = group;
    }

    public final int getGroup() {
        return group;
    }

}
