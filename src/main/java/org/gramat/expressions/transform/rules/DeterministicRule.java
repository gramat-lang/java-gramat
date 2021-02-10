package org.gramat.expressions.transform.rules;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionFactory;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.transform.TransformRule;
import org.gramat.util.ExpressionList;

public class DeterministicRule extends TransformRule {

    @Override
    protected Expression trySequence(Sequence sequence) {
        if (sequence.items.containsOf(Optional.class)) {
            return resolveOptionals(sequence.items);
        }

        return null;
    }

    static Expression resolveOptionals(ExpressionList newItems) {
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

        if (seq.isPresent() && op1.isPresent() && op2.isEmpty()) {
            // seq: (x y)
            // op1: (z)
            // op2: ()
            return resolveMandatoryOptional(seq, op1);
        }
        else if (seq.isEmpty()) {
            // seq: ()
            // op1: (x y z)
            // op2: (y z)
            return resolveOptionalMandatory(op1, op2);
        }
        else {
            // seq: (x)
            // op1: (y z)
            // op2: (z)
            // (x [y] z) → (x ((y z)|(z)))
            return ExpressionFactory.sequence(
                    ExpressionFactory.sequence(seq.build()),
                    ExpressionFactory.alternation(
                            ExpressionFactory.sequence(op1.build()),
                            ExpressionFactory.sequence(op2.build())
                    )
            );
        }
    }

    private static Expression resolveOptionalMandatory(ExpressionList.Builder op1, ExpressionList.Builder op2) {
        // ([x] y z) → ((x y z)|(y z))

        if (op2.isEmpty()) {
            return null;
        }

        return ExpressionFactory.alternation(
                ExpressionFactory.sequence(op1.build()),
                ExpressionFactory.sequence(op2.build())
        );
    }

    static Expression resolveMandatoryOptional(ExpressionList.Builder mandatory, ExpressionList.Builder optional) {
        var lastMandatory = mandatory.removeLast();

        optional.add(0, lastMandatory);

        if (mandatory.isPresent()) {
            // (x y [z]) → (x (y z|y))
            return ExpressionFactory.alternation(
                    ExpressionFactory.sequence(mandatory.build()),
                    ExpressionFactory.sequence(optional.build()),
                    lastMandatory
            );
        }

        // (y [z]) → (y z | y)
        return ExpressionFactory.alternation(
                ExpressionFactory.sequence(optional.build()),
                lastMandatory
        );
    }
}
