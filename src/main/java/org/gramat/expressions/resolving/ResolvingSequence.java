package org.gramat.expressions.resolving;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionFactory;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.misc.Nop;
import org.gramat.util.ExpressionList;

public interface ResolvingSequence {
    static Expression resolve(ResolvingContext rc, Sequence sequence) {
        var newItems = ResolvingExpression.resolveAll(rc, sequence.items);

        if (newItems.isEmpty()) {
            rc.hit("() → NOP");
            return new Nop(sequence.beginLocation, sequence.endLocation);
        }
        else if (newItems.size() == 1) {
            rc.hit("(x) → x");
            return newItems.get(0);
        }
        else if (newItems.containsOf(Sequence.class)) {
            return flattenSequence(rc, newItems);
        }
        else if (newItems.containsOf(Optional.class)) {
            return resolveOptionals(rc, newItems);
        }

        return ExpressionFactory.sequence(newItems);
    }

    static Sequence flattenSequence(ResolvingContext rc, ExpressionList items) {
        var newItems = ExpressionList.builder();

        for (var item : items) {
            if (item instanceof Sequence) {
                var subSeq = (Sequence) item;

                newItems.addAll(subSeq.items);
            }
            else {
                newItems.add(item);
            }
        }

        rc.hit("(x (y z)) → (x y z)");
        return ExpressionFactory.sequence(newItems.build());
    }

    static Expression resolveOptionals(ResolvingContext rc, ExpressionList newItems) {
        var seq = ExpressionList.builder();
        var op1 = ExpressionList.builder();
        var op2 = ExpressionList.builder();

        var afterOptional = true;

        for (var item : newItems) {
            if (afterOptional) {
                if (item instanceof Optional) {
                    var optional = (Optional) item;

                    op1.add(optional.content);

                    afterOptional = false;
                }
                else {
                    seq.add(item);
                }
            }
            else {
                op1.add(item);
                op2.add(item);
            }
        }

        if (op2.isEmpty()) {
            // seq: (x y)
            // op1: (z)
            // op2: ()
            var expr1 = ExpressionFactory.sequence(seq.build());
            seq.addAll(op1);
            var expr2 = ExpressionFactory.sequence(seq.build());
            rc.hit("(x y [z]) → ((x y)|(x y z))");
            return ExpressionFactory.alternation(expr1, expr2);
        }
        else if (seq.isEmpty()) {
            // seq: ()
            // op1: (x y z)
            // op2: (y z)
            rc.hit("([x] y z) → ((x y z)|(y z))");
            return ExpressionFactory.alternation(
                    ExpressionFactory.sequence(op1.build()),
                    ExpressionFactory.sequence(op2.build())
            );
        }
        else {
            // seq: (x)
            // op1: (y z)
            // op2: (z)
            rc.hit("(x [y] z) → (x ((y z)|(z)))");
            return ExpressionFactory.sequence(
                    ExpressionFactory.sequence(seq.build()),
                    ExpressionFactory.alternation(
                            ExpressionFactory.sequence(op1.build()),
                            ExpressionFactory.sequence(op2.build())
                    )
            );
        }
    }
}
