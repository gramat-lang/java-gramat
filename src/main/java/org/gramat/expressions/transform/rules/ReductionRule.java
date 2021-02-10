package org.gramat.expressions.transform.rules;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionFactory;
import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.transform.TransformRule;

public class ReductionRule extends TransformRule {

    @Override
    protected Expression trySequence(Sequence sequence) {
        if (sequence.items.isEmpty()) {
            return ExpressionFactory.nop(sequence.beginLocation);
        }
        else if (sequence.items.size() == 1) {
            return sequence.items.get(0);
        }
        return null;
    }

    @Override
    protected Expression tryAlternation(Alternation alternation) {
        if (alternation.items.isEmpty()) {
            return ExpressionFactory.nop(alternation.beginLocation);
        }
        else if (alternation.items.size() == 1) {
            return alternation.items.get(0);
        }
        return null;
    }

}
