package org.gramat.expressions.resolving;

import org.gramat.expressions.Expression;
import org.gramat.expressions.misc.Reference;

public interface ResolvingReference {
    static Expression resolve(ResolvingContext rc, Reference ref) {
        var name = ref.name;

        if (rc.recursiveNames.contains(name)) {
            return ref;
        }

        var originalTarget = rc.originalRules.find(name);

        return ResolvingExpression.resolve(rc, originalTarget);
    }
}
