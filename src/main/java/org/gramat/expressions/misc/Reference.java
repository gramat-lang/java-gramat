package org.gramat.expressions.misc;

import org.gramat.expressions.ExpressionFinal;
import org.gramat.util.Definition;

public class Reference extends ExpressionFinal {

    public final String name;

    public Reference(String name) {
        this.name = name;
    }

    @Override
    protected void define(Definition def) {
        def.attr("name", name);
    }

}
