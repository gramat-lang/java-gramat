package org.gramat.actions;

import org.gramat.eval.EvalEngine;
import org.gramat.util.DefinedObject;

public abstract class Action extends DefinedObject {

    public final int id;

    protected Action(int id) {
        this.id = id;
    }

    public abstract void execute(EvalEngine engine);

}
