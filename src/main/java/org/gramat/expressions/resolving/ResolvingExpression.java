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

public interface ResolvingExpression {
    static Expression resolve(ResolvingContext rc, Expression expr) {
        if (expr instanceof LiteralChar
                || expr instanceof LiteralRange
                || expr instanceof Wild) {
            return expr;
        }
        else if (expr instanceof Alternation) {
            return ResolvingAlternation.resolve(rc, (Alternation) expr);
        }
        else if (expr instanceof Optional) {
            return ResolvingOptional.resolve(rc, (Optional) expr);
        }
        else if (expr instanceof Cycle) {
            return ResolvingCycle.resolve(rc, (Cycle) expr);
        }
        else if (expr instanceof Sequence) {
            return ResolvingSequence.resolve(rc, (Sequence) expr);
        }
        else if (expr instanceof ActionExpression) {
            return ResolvingAction.resolve(rc, (ActionExpression) expr);
        }
        else if (expr instanceof Reference) {
            return ResolvingReference.resolve(rc, (Reference) expr);
        }
        else {
            throw new GramatException("unsupported value: " + expr);
        }
    }

}
