package org.gramat.expressions.actions;

import org.gramat.expressions.ExpressionFinal;
import org.gramat.util.Definition;

public class PropertyEnd extends ExpressionFinal {

    public final String nameHint;

    public PropertyEnd(String nameHint) {
        this.nameHint = nameHint;
    }

    @Override
    protected void define(Definition def) {
        def.attr("nameHint", nameHint);
    }
}
