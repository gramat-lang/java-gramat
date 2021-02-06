package org.gramat.actions;

import org.gramat.eval.EvalEngine;
import org.gramat.util.Definition;

public class ObjectEnd extends Action {

    public static final String KEY = "object-end";

    public static Action create(int id, ActionTemplate actionTemplate) {
        throw new UnsupportedOperationException();
    }

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
