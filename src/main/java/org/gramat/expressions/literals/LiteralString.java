package org.gramat.expressions.literals;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionFinal;
import org.gramat.inputs.Location;
import org.gramat.runtime.RExpression;
import org.gramat.util.Definition;
import org.gramat.util.WalkFunction;

public class LiteralString extends ExpressionFinal {

    public final String value;

    public LiteralString(Location begin, Location end, String value) {
        super(begin, end);
        this.value = value;
    }

    @Override
    protected void define(Definition def) {
        def.attr("value", value);
    }
}
