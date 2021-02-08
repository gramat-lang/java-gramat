package org.gramat.expressions.resolving;

import org.gramat.expressions.groups.Cycle;

public interface ResolvingCycle {
    static Cycle resolve(ResolvingContext rc, Cycle expr) {
        var resolvedContent = ResolvingExpression.resolve(rc, expr.content);
        
        return new Cycle(expr.beginLocation, expr.endLocation, resolvedContent);
    }
}
