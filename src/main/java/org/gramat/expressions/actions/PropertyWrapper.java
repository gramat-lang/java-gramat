package org.gramat.expressions.actions;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionContent;
import org.gramat.util.Definition;

public class PropertyWrapper extends ExpressionContent {

    public final Expression content;
    public final String nameHint;

    public PropertyWrapper(Expression content, String nameHint) {
        this.content = content;
        this.nameHint = nameHint;
    }

    @Override
    protected void define(Definition def) {
        def.attr("content", content);
        def.attr("nameHint", nameHint);
    }

    @Override
    public Expression getContent() {
        return content;
    }
}
