package org.gramat.expressions.resolving;

import org.gramat.expressions.groups.Optional;

public interface ResolvingOptional {
    static Optional resolve(ResolvingContext rc, Optional expr) {
        return new Optional(expr.beginLocation, expr.endLocation, ResolvingExpression.resolve(rc, expr.content));
    }
}
