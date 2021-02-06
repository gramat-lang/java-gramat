package org.gramat.actions;

import org.gramat.eval.EvalEngine;
import org.gramat.util.Definition;

public class HeapPop extends Action {

    private final Object token;

    public HeapPop(int id, Object token) {
        super(id);
        this.token = token;
    }

    @Override
    public void execute(EvalEngine engine) {
        engine.heap.pop(id, token);
    }

    @Override
    protected void define(Definition def) {
        def.attr("token", token);
    }
}
