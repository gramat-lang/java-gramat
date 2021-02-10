package org.gramat.expressions.transform.rules;

import org.gramat.actions.Action;
import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionFactory;
import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.groups.Cycle;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.misc.ActionExpression;
import org.gramat.expressions.transform.TransformRule;
import org.gramat.util.ExpressionList;

import java.util.Objects;

public class PromotionRule extends TransformRule {

    @Override
    protected Expression tryAction(ActionExpression action) {
        // <( [x] )> → [ <(x)> ]
        if (action.content instanceof Optional) {
            var optional = (Optional) action.content;
            return ExpressionFactory.optional(
                    ExpressionFactory.action(action, optional.content)
            );
        }
        return null;
    }

    @Override
    protected Expression tryCycle(Cycle cycle) {
        // {+ [x] } → [ {+x} ]
        if (cycle.content instanceof Optional) {
            var optional = (Optional)cycle.content;
            return ExpressionFactory.optional(
                    ExpressionFactory.cycle(optional.content)
            );
        }
        return null;
    }

    @Override
    protected Expression tryAlternation(Alternation alternation) {
        // (x|[y]|z) → [ x|y|z ]
        if (alternation.items.containsOf(Optional.class)) {
            return promoteAlternationOptionals(alternation);
        }
        // <(x)>|<(y)>|<(z)> → <(x|y|z)>
        else if (alternation.items.containsOf(ActionExpression.class)) {
            return tryPromoteAlternationActions(alternation);
        }
        return null;
    }

    private Expression promoteAlternationOptionals(Alternation alternation) {
        var newItems = ExpressionList.builder();

        for (var item : alternation.items) {
            if (item instanceof Optional) {
                var optional = (Optional) item;

                newItems.add(optional.content);
            }
            else {
                newItems.add(item);
            }
        }

        return ExpressionFactory.optional(
                ExpressionFactory.alternation(newItems.build())
        );
    }

    private Expression tryPromoteAlternationActions(Alternation alternation) {
        if (alternation.items.isPresent() && alternation.items.get(0) instanceof ActionExpression) {
            var baseAction = (ActionExpression)alternation.items.get(0);

            for (var item : alternation.items) {
                if (item instanceof ActionExpression) {
                    var itemAction = (ActionExpression)item;

                    if (!Objects.equals(baseAction.argument, itemAction.argument)
                            || !Objects.equals(baseAction.scheme, itemAction.scheme)) {
                        return null;
                    }
                }
                else {
                    return null;
                }
            }

            var newItems = ExpressionList.builder();

            for (var item : alternation.items) {
                var itemAction = (ActionExpression) item;

                newItems.add(itemAction.content);
            }

            return ExpressionFactory.action(baseAction, ExpressionFactory.alternation(newItems));
        }
        return null;
    }
}
