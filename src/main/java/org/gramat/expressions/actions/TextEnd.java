package org.gramat.expressions.actions;

import org.gramat.expressions.ExpressionFinal;
import org.gramat.util.Definition;

public class TextEnd extends ExpressionFinal {

    public final String parser;

    public TextEnd(String parser) {
        this.parser = parser;
    }

    @Override
    protected void define(Definition def) {
        def.attr("parser", parser);
    }
}
