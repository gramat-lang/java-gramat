package org.gramat.expressions.actions;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionContent;
import org.gramat.inputs.Location;
import org.gramat.util.Definition;

public class TextWrapper extends ExpressionContent {

    public final Expression content;
    public final String parser;

    public TextWrapper(Location begin, Location end, Expression content, String parser) {
        super(begin, end);
        this.content = content;
        this.parser = parser;
    }

    @Override
    protected void define(Definition def) {
        def.attr("content", content);
        def.attr("parser", parser);
    }

    @Override
    public Expression getContent() {
        return content;
    }
}
