package org.gramat.actions;

import org.gramat.eval.EvalEngine;
import org.gramat.util.Definition;

public class NameEnd extends Action {

    public static final String KEY = "name-end";

    public static Action create(int id, ActionTemplate actionTemplate) {
        throw new UnsupportedOperationException();
    }

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
