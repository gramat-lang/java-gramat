package org.gramat.expressions.literals;

import org.gramat.expressions.ExpressionFinal;
import org.gramat.inputs.Location;
import org.gramat.util.Definition;

public class LiteralChar extends ExpressionFinal {// TODO remove this class?

    public final char value;

    public LiteralChar(Location begin, Location end, char value) {
        super(begin, end);
        this.value = value;
    }

    @Override
    protected void define(Definition def) {
        def.attr("value", value);
    }

}
