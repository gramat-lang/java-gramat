package org.gramat.expressions.resolving;

import org.gramat.exceptions.GramatException;
import org.gramat.expressions.Expression;
import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.groups.Cycle;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.literals.LiteralChar;
import org.gramat.expressions.literals.LiteralRange;
import org.gramat.expressions.misc.ActionExpression;
import org.gramat.expressions.misc.Reference;
import org.gramat.expressions.misc.Wild;
import org.gramat.formatting.ExpressionFormatter;
import org.gramat.util.ExpressionList;
import org.gramat.util.PP;

public interface ResolvingExpression {
    static Expression resolve(ResolvingContext rc, Expression expr) {
        if (expr instanceof LiteralChar
                || expr instanceof LiteralRange
                || expr instanceof Wild) {
            return expr;
        }

        Expression result;
        var hits0 = rc.getHits();

        if (expr instanceof Alternation) {
            result = ResolvingAlternation.resolve(rc, (Alternation) expr);
        }
        else if (expr instanceof Optional) {
            result = ResolvingOptional.resolve(rc, (Optional) expr);
        }
        else if (expr instanceof Cycle) {
            result = ResolvingCycle.resolve(rc, (Cycle) expr);
        }
        else if (expr instanceof Sequence) {
            result = ResolvingSequence.resolve(rc, (Sequence) expr);
        }
        else if (expr instanceof ActionExpression) {
            result = ResolvingAction.resolve(rc, (ActionExpression) expr);
        }
        else if (expr instanceof Reference) {
            result = ResolvingReference.resolve(rc, (Reference) expr);
        }
        else {
            throw new GramatException("unsupported value: " + expr);
        }

        if (hits0 != rc.getHits()) {
            System.out.println(">".repeat(80));
            new ExpressionFormatter(System.out).write(expr);
            System.out.println();
            System.out.println("-".repeat(80));
            new ExpressionFormatter(System.out).write(result);
            System.out.println();
            System.out.println("<".repeat(80));
        }

        return result;
    }

    static ExpressionList resolveAll(ResolvingContext rc, ExpressionList items) {
        return items.map(item -> resolve(rc, item));
    }
}
