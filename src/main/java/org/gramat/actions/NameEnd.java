package org.gramat.actions;

import org.gramat.eval.EvalEngine;
import org.gramat.util.Definition;

public class NameEnd extends Action {
    public NameEnd(int id) {
        super(id);
    }

    @Override
    public void execute(EvalEngine engine) {
        engine.builder.performPopName(id);
    }

    @Override
    protected void define(Definition def) {
        // nothing more
    }
}
