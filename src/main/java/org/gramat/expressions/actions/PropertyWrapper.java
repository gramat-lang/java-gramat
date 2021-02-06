package org.gramat.expressions.actions;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionContent;
import org.gramat.inputs.Location;
import org.gramat.util.Definition;

public class PropertyWrapper extends ExpressionContent {

    public final Expression content;
    public final String nameHint;

    public PropertyWrapper(Location begin, Location end, Expression content, String nameHint) {
        super(begin, end);
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
