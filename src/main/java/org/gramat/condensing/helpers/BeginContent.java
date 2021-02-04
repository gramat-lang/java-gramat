package org.gramat.condensing.helpers;

import org.gramat.exceptions.GramatException;
import org.gramat.expressions.Expression;
import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Repetition;
import org.gramat.expressions.groups.Sequence;

import java.util.List;
import java.util.Objects;

public class BeginContent {

    public static BeginContent of(Expression expr) {
        if (expr instanceof Sequence) {
            return ofSequence((Sequence)expr);
        }
        else if (expr instanceof Alternation || expr instanceof Optional || expr instanceof Repetition) {
            throw new GramatException("unexpected expression: " + expr.getClass());
        }
        else {
            return new BeginContent(expr, List.of());
        }
    }

    public static BeginContent ofSequence(Sequence seq) {
        var begin = seq.items.first();

        if (seq.items.size() == 1) {
            return new BeginContent(begin, List.of());
        }

        var content = seq.items.subList(1, seq.items.size());

        return new BeginContent(begin, content);
    }

    public final Expression begin;
    public final List<Expression> content;

    private BeginContent(Expression begin, List<Expression> content) {
        this.begin = Objects.requireNonNull(begin);
        this.content = Objects.requireNonNull(content);
    }
}
