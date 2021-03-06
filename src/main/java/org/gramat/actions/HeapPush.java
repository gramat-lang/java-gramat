package org.gramat.actions;

import org.gramat.eval.EvalEngine;
import org.gramat.util.Definition;

import java.util.Objects;

public class HeapPush extends Action {

    public final Object token;

    public HeapPush(int id, Object token) {
        super(id);
        this.token = Objects.requireNonNull(token);
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
