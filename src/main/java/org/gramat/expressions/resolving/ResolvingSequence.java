package org.gramat.expressions.resolving;

import org.gramat.expressions.Expression;
import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.misc.Halt;
import org.gramat.expressions.misc.Nop;
import org.gramat.util.ExpressionList;

public interface ResolvingSequence {
    static Expression resolve(ResolvingContext rc, Sequence expr) {
        if (expr.items.isEmpty()) {
            return new Nop(expr.beginLocation, expr.endLocation);
        }
        else if (expr.items.size() == 1) {
            return ResolvingExpression.resolve(rc, expr.items.get(0));
        }

        return mergeSubSequences(rc, expr);
    }

    static Sequence mergeSubSequences(ResolvingContext rc, Sequence expr) {
        var items = ExpressionList.builder();

        for (var rawItem : expr.items) {
            var item = ResolvingExpression.resolve(rc, rawItem);

            if (item instanceof Sequence) {
                var subSeq = (Sequence) item;

                items.addAll(subSeq.items);
            }
            else {
                items.add(item);
            }
        }

        return new Sequence(expr.beginLocation, expr.endLocation, items.build());
    }

    static Expression removeOptionalsFromSequence(ResolvingContext rc, Sequence seq) {
        // TODO move
        // ([a] b c) → (a b c) | (b c)
        // (a [b] c) → a (b c | c)
        // (a b [c]) → a b (c | ^)

        var part1 = ExpressionList.builder();
        var part2 = ExpressionList.builder();
        var part3 = ExpressionList.builder();

        var afterOptional = true;

        for (var item : seq.items) {
            if (item instanceof Optional) {
                part2.add(item);

                afterOptional = false;
            }
            else if (afterOptional) {
                part1.add(item);
            }
            else {
                part2.add(item);
                part3.add(item);
            }
        }

        if (part3.isEmpty()) {
            var haltLocation = seq.items.last().endLocation;
            part3.add(new Halt(haltLocation, haltLocation));
        }

        Expression result = new Alternation(seq.beginLocation, seq.endLocation, ExpressionList.of(
                new Sequence(part2.first().beginLocation, part2.last().endLocation, part2.build()),
                new Sequence(part3.first().beginLocation, part3.last().endLocation, part3.build())
        ));

        if (!part1.isEmpty()) {
            result = new Sequence(seq.beginLocation, seq.endLocation, ExpressionList.of(
                    new Sequence(part1.first().beginLocation, part1.last().endLocation, part1.build()),
                    result
            ));
        }

        return result;
    }
}
