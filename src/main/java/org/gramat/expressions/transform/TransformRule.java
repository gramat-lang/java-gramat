package org.gramat.expressions.transform;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionFactory;
import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.groups.Cycle;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.literals.LiteralChar;
import org.gramat.expressions.literals.LiteralRange;
import org.gramat.expressions.misc.ActionExpression;
import org.gramat.expressions.misc.Reference;
import org.gramat.util.ExpressionList;

public class TransformRule {

    protected Expression tryAction(ActionExpression action) { return null; }
    protected Expression tryAlternation(Alternation alternation) { return null; }
    protected Expression tryCycle(Cycle cycle) { return null; }
    protected Expression tryOptional(Optional optional) { return null; }
    protected Expression tryReference(Reference reference) { return null; }
    protected Expression trySequence(Sequence sequence) { return null; }

    public final Expression transform(TransformContext ctx, Expression expr) {
        var last = expr;

        while (true) {
            var result = tryExpressionAdapter(ctx, last);

            if (result == null) {
                break;
            }

            last = result;
        }

        return last;
    }

    private Expression tryExpressionAdapter(TransformContext ctx, Expression expr) {
        if (expr instanceof ActionExpression) {
            return tryActionAdapter(ctx, (ActionExpression) expr);
        }
        else if (expr instanceof Alternation) {
            return tryAlternationAdapter(ctx, (Alternation) expr);
        }
        else if (expr instanceof Cycle) {
            return tryCycleAdapter(ctx, (Cycle) expr);
        }
        else if (expr instanceof Optional) {
            return tryOptionalAdapter(ctx, (Optional)expr);
        }
        else if (expr instanceof Reference) {
            return tryReferenceAdapter(ctx, (Reference)expr);
        }
        else if (expr instanceof Sequence) {
            return trySequenceAdapter(ctx, (Sequence)expr);
        }
        else if (expr instanceof LiteralChar || expr instanceof LiteralRange) {
            return null;
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    private Expression tryActionAdapter(TransformContext ctx, ActionExpression action) {
        // Custom
        var newAction = tryAction(action);
        if (newAction != null) {
            ctx.track(this, action, newAction);
            return newAction;
        }

        // Fallback
        var newContent = tryExpressionAdapter(ctx, action.content);
        if (newContent != null) {
            return ExpressionFactory.action(action, newContent);
        }
        return null;
    }

    private Expression tryAlternationAdapter(TransformContext ctx, Alternation alternation) {
        // Custom
        var newAlternation = tryAlternation(alternation);
        if (newAlternation != null) {
            ctx.track(this, alternation, newAlternation);
            return newAlternation;
        }

        // Fallback
        var newItems = tryItemsAdapter(ctx, alternation.items);
        if (newItems != null) {
            return ExpressionFactory.alternation(newItems);
        }
        return null;
    }

    private Expression tryCycleAdapter(TransformContext ctx, Cycle cycle) {
        // Custom
        var newCycle = tryCycle(cycle);
        if (newCycle != null) {
            ctx.track(this, cycle, newCycle);
            return newCycle;
        }

        // Fallback
        var newContent = tryExpressionAdapter(ctx, cycle.content);
        if (newContent != null) {
            return ExpressionFactory.cycle(newContent);
        }
        return null;
    }

    private Expression tryOptionalAdapter(TransformContext ctx, Optional optional) {
        // Custom
        var newOptional = tryOptional(optional);
        if (newOptional != null) {
            ctx.track(this, optional, newOptional);
            return newOptional;
        }

        // Fallback
        var newContent = tryExpressionAdapter(ctx, optional.content);
        if (newContent != null) {
            return ExpressionFactory.optional(newContent);
        }
        return null;
    }

    private Expression tryReferenceAdapter(TransformContext ctx, Reference reference) {
        // Custom
        var newReference = tryReference(reference);
        if (newReference != null) {
            ctx.track(this, reference, newReference);
            return newReference;
        }

        // Fallback
        return null;
    }

    private Expression trySequenceAdapter(TransformContext ctx, Sequence sequence) {
        // Custom
        var newSequence = trySequence(sequence);
        if (newSequence != null) {
            ctx.track(this, sequence, newSequence);
            return newSequence;
        }

        // Fallback
        var newItems = tryItemsAdapter(ctx, sequence.items);
        if (newItems != null) {
            return ExpressionFactory.sequence(newItems);
        }
        return null;
    }

    private ExpressionList tryItemsAdapter(TransformContext ctx, ExpressionList items) {
        var newItems = ExpressionList.builder();
        var modified = false;

        for (var item : items) {
            var newItem = tryExpressionAdapter(ctx, item);

            if (newItem != null) {
                modified = true;
            }
            else {
                newItem = item;
            }

            newItems.add(newItem);
        }

        if (modified) {
            return newItems.build();
        }
        return null;
    }

}
