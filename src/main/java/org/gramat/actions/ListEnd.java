package org.gramat.actions;

import org.gramat.eval.EvalEngine;
import org.gramat.util.Definition;

public class ListEnd extends Action {

    public final String typeHint;

    public ListEnd(int id, String typeHint) {
        super(id);
        this.typeHint = typeHint;
    }

    @Override
    public void execute(EvalEngine engine) {
        engine.builder.performPopList(id, typeHint);
    }

    @Override
    protected void define(Definition def) {
        def.attr("typeHint", typeHint);
    }
}
