package org.gramat.actions;

import org.gramat.eval.EvalEngine;
import org.gramat.util.Definition;

public class PropertyEnd extends Action {

    public static final String KEY = "property-end";

    public static Action create(int id, ActionTemplate actionTemplate) {
        throw new UnsupportedOperationException();
    }

    public final String nameHint;

    public PropertyEnd(int id, String nameHint) {
        super(id);
        this.nameHint = nameHint;
    }

    @Override
    public void execute(EvalEngine engine) {
        engine.builder.performPopProperty(id, nameHint);
    }

    @Override
    protected void define(Definition def) {
        def.attr("nameHint", nameHint);
    }
}
