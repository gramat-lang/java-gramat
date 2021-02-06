package org.gramat.expressions.groups;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionContent;
import org.gramat.inputs.Location;
import org.gramat.util.Definition;

import java.util.Objects;

public class Optional extends ExpressionContent {

    public final Expression content;

    public Optional(Location begin, Location end, Expression content) {
        super(begin, end);
        this.content = Objects.requireNonNull(content);
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
