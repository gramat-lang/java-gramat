package org.gramat.expressions.misc;

import org.gramat.expressions.ExpressionFinal;
import org.gramat.inputs.Location;
import org.gramat.util.Definition;

public class Reference extends ExpressionFinal {

    public final String name;

    public Reference(Location begin, Location end, String name) {
        super(begin, end);
        this.name = name;
    }

    @Override
    protected void define(Definition def) {
        def.attr("name", name);
    }

}
