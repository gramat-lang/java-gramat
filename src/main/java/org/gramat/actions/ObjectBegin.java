package org.gramat.actions;

import org.gramat.eval.EvalEngine;
import org.gramat.util.Definition;

public class ObjectBegin extends Action {

    public ObjectBegin(int id) {
        super(id);
    }

    @Override
    public void execute(EvalEngine engine) {
        engine.builder.performPushObject(id);
    }

    @Override
    protected void define(Definition def) {
        // nothing more
    }
}
