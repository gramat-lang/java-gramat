package org.gramat.expressions.actions;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionContent;
import org.gramat.inputs.Location;
import org.gramat.util.Definition;

public class NameWrapper extends ExpressionContent {

    public final Expression content;

    public NameWrapper(Location begin, Location end, Expression content) {
        super(begin, end);
        this.content = content;
    }

    @Override
    protected void define(Definition def) {
        def.attr("content", content);
    }

    @Override
    public Expression getContent() {
        return content;
    }
}
