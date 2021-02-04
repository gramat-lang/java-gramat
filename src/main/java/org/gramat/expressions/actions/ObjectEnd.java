package org.gramat.expressions.actions;

import org.gramat.expressions.ExpressionFinal;
import org.gramat.util.Definition;

public class ObjectEnd extends ExpressionFinal {

    public final String typeHint;

    public ObjectEnd(String typeHint) {
        this.typeHint = typeHint;
    }

    @Override
    protected void define(Definition def) {
        def.attr("typeHint", typeHint);
    }
}
