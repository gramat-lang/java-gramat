package org.gramat.actions;

import org.gramat.eval.EvalEngine;
import org.gramat.util.Definition;

import java.util.Objects;

public class HeapPop extends Action {

    private final Object token;

    public HeapPop(int id, Object token) {
        super(id);
        this.token = Objects.requireNonNull(token);
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
