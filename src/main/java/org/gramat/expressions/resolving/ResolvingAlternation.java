package org.gramat.expressions.resolving;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionFactory;
import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.misc.Nop;
import org.gramat.util.ExpressionList;

public interface ResolvingAlternation {
    static Expression resolve(ResolvingContext rc, Alternation alt) {
        var newItems = ResolvingExpression.resolveAll(rc, alt.items);

        if (newItems.isEmpty()) {
            rc.hit("() → NOP");
            return new Nop(alt.beginLocation, alt.endLocation);
        }
        else if (newItems.size() == 1) {
            rc.hit("(x) → x");
            return newItems.get(0);
        }
        else if (newItems.containsOf(Alternation.class)) {
            return flattenAlternations(rc, newItems);
        }
        else if (newItems.containsOf(Optional.class)) {
            return promoteOptionals(rc, newItems);
        }

        return ExpressionFactory.alternation(newItems);
    }

    static Expression flattenAlternations(ResolvingContext rc, ExpressionList items) {
        var newItems = ExpressionList.builder();

        for (var item : items) {
            if (item instanceof Alternation) {
                var subAlt = (Alternation) item;

                newItems.addAll(subAlt.items);
            }
            else {
                newItems.add(item);
            }
        }

        rc.hit("(x|(y|z)) → (x|y|z)");
        return ExpressionFactory.alternation(newItems.build());
    }

    static Expression promoteOptionals(ResolvingContext rc, ExpressionList items) {
        var newItems = ExpressionList.builder();

        for (var item : items) {
            if (item instanceof Optional) {
                var optional = (Optional) item;

                newItems.add(optional.content);
            }
            else {
                newItems.add(item);
            }
        }

        rc.hit("(x|[y]|z) → [x|y|z]");
        return ExpressionFactory.optional(
                ExpressionFactory.alternation(newItems.build())
        );
    }


}
