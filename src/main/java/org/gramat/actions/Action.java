package org.gramat.actions;

import org.gramat.eval.EvalEngine;
import org.gramat.util.Definition;

public abstract class Action {

    public abstract void execute(EvalEngine engine);

    protected abstract void define(Definition def);

    private Definition definition() {
        var def = new Definition(getClass());

        define(def);

        return def;
    }

    @Override
    public final String toString() {
        return definition().computeString();
    }
}
