package org.gramat.expressions.actions;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionContent;
import org.gramat.util.Definition;

public class TextWrapper extends ExpressionContent {

    public final Expression content;
    public final String parser;

    public TextWrapper(Expression content, String parser) {
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
