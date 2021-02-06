package org.gramat.actions;

import org.gramat.eval.EvalEngine;
import org.gramat.util.Definition;

public class HeapPush extends Action {

    public static final String KEY = "heap-push";

    public static Action create(int id, ActionTemplate actionTemplate) {
        throw new UnsupportedOperationException();
    }

    public final Object token;

    public HeapPush(int id, Object token) {
        super(id);
        this.token = token;
    }

    @Override
    public void execute(EvalEngine engine) {
        engine.heap.push(id, token);
    }

    @Override
    protected void define(Definition def) {
        def.attr("token", token);
    }
}
