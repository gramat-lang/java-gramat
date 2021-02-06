package org.gramat.actions;

import org.gramat.eval.EvalEngine;
import org.gramat.util.Definition;

public class NameBegin extends Action {

    public static final String KEY = "name-begin";

    public static Action create(int id, ActionTemplate actionTemplate) {
        throw new UnsupportedOperationException();
    }

    public NameBegin(int id) {
        super(id);
    }

    @Override
    public void execute(EvalEngine engine) {
        engine.builder.performPushName(id);
    }

    @Override
    protected void define(Definition def) {
        // nothing more
    }
}
