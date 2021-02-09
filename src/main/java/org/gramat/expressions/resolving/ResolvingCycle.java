package org.gramat.expressions.resolving;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionFactory;
import org.gramat.expressions.groups.Cycle;
import org.gramat.expressions.groups.Optional;

public interface ResolvingCycle {
    static Expression resolve(ResolvingContext rc, Cycle cycle) {
        var newContent = ResolvingExpression.resolve(rc, cycle.content);

        // {+(x y z)} ✅
        // {+(x|y|z)} ✅
        // {+<?(x y z)?>} ✅
        // {+ref} ✅
        if (newContent instanceof Optional) {
            rc.hit("{+[x]} → [{+x}]");
            return promoteOptional((Optional) newContent);
        }

        return ExpressionFactory.cycle(newContent);
    }

    static Expression promoteOptional(Optional optional) {
        return ExpressionFactory.optional(
                ExpressionFactory.cycle(optional.content)
        );
    }
}
