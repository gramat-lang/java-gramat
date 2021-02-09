package org.gramat.expressions.resolving;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionFactory;
import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.misc.ActionExpression;
import org.gramat.util.ExpressionList;

public interface ResolvingAction {
    static Expression resolve(ResolvingContext rc, ActionExpression action) {
        var newContent = ResolvingExpression.resolve(rc, action.content);

        if (newContent instanceof Alternation) {
            return promoteAlternation(rc, action, (Alternation) newContent);
        }
        else if (newContent instanceof Optional) {
            return promoteOptional(rc, action, (Optional) newContent);
        }

        return ExpressionFactory.action(action, newContent);
    }

    static Expression promoteAlternation(ResolvingContext rc, ActionExpression action, Alternation alternation) {
        var items = ExpressionList.builder();

        for (var item : alternation.items) {
            items.add(ExpressionFactory.action(action, item));
        }

        rc.hit("<?(x|y|z)?> → (<?(x)?>|<?(y)?>|<?(z)?>)");
        return ExpressionFactory.alternation(items.build());
    }

    static Expression promoteOptional(ResolvingContext rc, ActionExpression action, Optional optional) {
        rc.hit("<?([x])?> → [<?(x)?>]");
        return ExpressionFactory.optional(
                ExpressionFactory.action(action, optional.content)
        );
    }
}
