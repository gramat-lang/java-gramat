package org.gramat.expressions;

import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.groups.Cycle;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.misc.ActionExpression;
import org.gramat.inputs.Location;
import org.gramat.util.ExpressionList;

public interface ExpressionFactory {
    static Sequence sequence(Expression... items) {
        return sequence(ExpressionList.of(items));
    }

    static Sequence sequence(ExpressionList items) {
        if (items.isEmpty()) {
            throw new RuntimeException();
        }

        var first = items.first();
        var last = items.last();
        return new Sequence(first.beginLocation, last.endLocation, items);
    }

    static Expression alternation(Expression... items) {
        return alternation(ExpressionList.of(items));
    }

    static Expression alternation(ExpressionList items) {
        if (items.isEmpty()) {
            throw new RuntimeException();
        }

        Location begin = null;
        Location end = null;

        for (var item : items) {
            if (begin == null || item.beginLocation.getOffset() < begin.getOffset()) {
                begin = item.beginLocation;
            }

            if (end == null || item.endLocation.getOffset() > end.getOffset()) {
                end = item.endLocation;
            }
        }

        return new Alternation(begin, end, items);
    }

    static ActionExpression action(ActionExpression baseAction, Expression newContent) {
        return new ActionExpression(
                baseAction.beginLocation, baseAction.endLocation,
                baseAction.scheme, newContent, baseAction.argument);
    }

    static Optional optional(Expression content) {
        return new Optional(content.beginLocation, content.endLocation, content);
    }

    static Cycle cycle(Expression content) {
        return new Cycle(content.beginLocation, content.endLocation, content);
    }
}
