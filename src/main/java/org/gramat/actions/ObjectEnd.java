package org.gramat.actions;

import org.gramat.eval.EvalEngine;
import org.gramat.util.Definition;

public class ObjectEnd extends Action {
    public final String typeHint;

    public ObjectEnd(int id, String typeHint) {
        super(id);
        this.typeHint = typeHint;
    }

    @Override
    public void execute(EvalEngine engine) {
        engine.builder.performPopObject(id, typeHint);
    }

    @Override
    protected void define(Definition def) {
        def.attr("typeHint", typeHint);
    }
}
