package org.gramat.expressions.actions;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionContent;
import org.gramat.inputs.Location;
import org.gramat.util.Definition;

public class ObjectWrapper extends ExpressionContent {

    public final Expression content;
    public final String typeHint;

    public ObjectWrapper(Location begin, Location end, Expression content, String typeHint) {
        super(begin, end);
        this.content = content;
        this.typeHint = typeHint;
    }

    @Override
    protected void define(Definition def) {
        def.attr("content", content);
        def.attr("typeHint", typeHint);
    }

    @Override
    public Expression getContent() {
        return content;
    }
}
