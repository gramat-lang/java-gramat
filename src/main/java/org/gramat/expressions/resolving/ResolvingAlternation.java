package org.gramat.expressions.resolving;

import org.gramat.expressions.Expression;
import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.misc.Nop;
import org.gramat.util.ExpressionList;

public interface ResolvingAlternation {
    static Expression resolve(ResolvingContext rc, Alternation alt) {
        if (alt.items.isEmpty()) {
            return new Nop(alt.beginLocation, alt.endLocation);
        }
        else if (alt.items.size() == 1) {
            return alt.items.get(0);
        }

        return mergeSubAlternations(rc, alt);
    }

    static Alternation mergeSubAlternations(ResolvingContext rc, Alternation alt) {
        var newItems = ExpressionList.builder();

        for (var oldItem : alt.items) {
            var newItem = ResolvingExpression.resolve(rc, oldItem);

            if (newItem instanceof Alternation) {
                var subAlt = (Alternation) newItem;

                newItems.addAll(subAlt.items);
            }
            else {
                newItems.add(newItem);
            }
        }

        return new Alternation(alt.beginLocation, alt.endLocation, newItems.build());
    }

}
