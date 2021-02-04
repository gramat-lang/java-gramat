package org.gramat.expressions.literals;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionFinal;
import org.gramat.runtime.RExpression;
import org.gramat.util.Definition;
import org.gramat.util.WalkFunction;

public class LiteralString extends ExpressionFinal {

    public final String value;

    public LiteralString(String value) {
        this.value = value;
    }

    @Override
    public RExpression build() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void define(Definition def) {
        def.attr("value", value);
    }
}
