package org.gramat.expressions.misc;

import org.gramat.actions.design.ActionScheme;
import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionContent;
import org.gramat.inputs.Location;
import org.gramat.util.Definition;

public class ActionExpression extends ExpressionContent {

    public final ActionScheme scheme;
    public final Expression content;
    public final String argument;

    public ActionExpression(Location begin, Location end, ActionScheme scheme, Expression content, String argument) {
        super(begin, end);
        this.scheme = scheme;
        this.content = content;
        this.argument = argument;
    }

    @Override
    public Expression getContent() {
        return content;
    }

    @Override
    protected void define(Definition def) {
        def.attr("content", content);
        def.attr("argument", argument);
    }
}
