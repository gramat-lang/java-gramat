package org.gramat.expressions.groups;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionContent;
import org.gramat.inputs.Location;
import org.gramat.util.Definition;

public class Cycle extends ExpressionContent {

    public final Expression content;

    public Cycle(Location begin, Location end, Expression content) {
        super(begin, end);
        this.content = content;
    }

    @Override
    public Expression getContent() {
        return content;
    }

    @Override
    protected void define(Definition def) {
        def.attr("content", content);
    }
}
