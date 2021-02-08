package org.gramat.expressions.resolving;

import org.gramat.expressions.misc.ActionExpression;

public interface ResolvingAction {
    static ActionExpression resolve(ResolvingContext rc, ActionExpression expr) {
        var resolvedContent = ResolvingExpression.resolve(rc, expr.content);
        return new ActionExpression(expr.beginLocation, expr.endLocation, expr.scheme, resolvedContent, expr.argument);
    }
}
