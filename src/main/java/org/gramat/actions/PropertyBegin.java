package org.gramat.actions;

import org.gramat.eval.EvalEngine;
import org.gramat.util.Definition;

public class PropertyBegin extends Action {

    public PropertyBegin(int id) {
        super(id);
    }

    @Override
    public void execute(EvalEngine engine) {
        engine.builder.performPushProperty(id);
    }

    @Override
    protected void define(Definition def) {
        // nothing more
    }
}
