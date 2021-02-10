package org.gramat.expressions.transform.rules;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionFactory;
import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.groups.Cycle;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.transform.TransformRule;
import org.gramat.util.ExpressionList;

public class FlatteningRule extends TransformRule {

    @Override
    protected Expression tryOptional(Optional optional) {
        // [[x]] → [x]
        if (optional.content instanceof Optional) {
            return optional.content;
        }
        return null;
    }

    @Override
    protected Expression tryCycle(Cycle cycle) {
        // {+{+x}} → {+x}
        if (cycle.content instanceof Cycle) {
            return cycle.content;
        }
        return null;
    }

    @Override
    protected Expression trySequence(Sequence sequence) {
        if (sequence.items.containsOf(Sequence.class)) {
            var newItems = ExpressionList.builder();

            for (var item : sequence.items) {
                if (item instanceof Sequence) {
                    var subSeq = (Sequence) item;

                    newItems.addAll(subSeq.items);
                }
                else {
                    newItems.add(item);
                }
            }

            return ExpressionFactory.sequence(newItems.build());
        }
        return null;
    }

    @Override
    protected Expression tryAlternation(Alternation alternation) {
        if (alternation.items.containsOf(Alternation.class)) {
            var newItems = ExpressionList.builder();

            for (var item : alternation.items) {
                if (item instanceof Alternation) {
                    var subAlt = (Alternation) item;

                    newItems.addAll(subAlt.items);
                }
                else {
                    newItems.add(item);
                }
            }

            return ExpressionFactory.alternation(newItems.build());
        }

        return null;
    }
}
