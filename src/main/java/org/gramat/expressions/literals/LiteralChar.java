package org.gramat.expressions.literals;

import org.gramat.expressions.ExpressionFinal;
import org.gramat.runtime.RExpression;
import org.gramat.runtime.RLiteralChar;
import org.gramat.util.Definition;

public class LiteralChar extends ExpressionFinal {// TODO remove this class?

    public final char value;

    public LiteralChar(char value) {
        this.value = value;
    }

    @Override
    protected void define(Definition def) {
        def.attr("value", value);
    }

    @Override
    public RExpression build() {
        return new RLiteralChar(value);
    }
}
