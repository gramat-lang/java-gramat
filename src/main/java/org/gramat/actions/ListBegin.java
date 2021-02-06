package org.gramat.actions;

import org.gramat.eval.EvalEngine;
import org.gramat.util.Definition;

public class ListBegin extends Action {

    public static final String KEY = "list-begin";

    public static Action create(int id, ActionTemplate actionTemplate) {
        throw new UnsupportedOperationException();
    }

    public ListBegin(int id) {
        super(id);
    }

    @Override
    public void execute(EvalEngine engine) {
        engine.builder.performPushList(id);
    }

    @Override
    protected void define(Definition def) {
        // nothing more
    }
}
