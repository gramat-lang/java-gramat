package org.gramat.expressions.transform.rules;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionFactory;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.transform.TransformRule;
import org.gramat.formatting.ExpressionFormatter;
import org.gramat.util.ExpressionList;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class CombinationRule extends TransformRule {

    @Override
    protected Expression trySequence(Sequence sequence) {
        if (sequence.items.containsOf(Optional.class)) {
            return combineOptionals(sequence);
        }
        return null;
    }

    private Expression combineOptionals(Sequence sequence) {
        var beginList = new ArrayList<Expression>();
        var targetList = new ArrayList<Optional>();
        var endList = new ArrayList<Expression>();

        // [a] [b] c d → () ([a] [b]) (c d)
        // a [b] [c] d → (a) ([b] [c]) (d)
        // a b [c] [d] → (a b) ([c] [d]) ()
        // a b c d     → (a b c d) () ()
        for (var item : sequence.items) {
            if (item instanceof Optional && endList.isEmpty()) {
                targetList.add((Optional) item);
            }
            else if (targetList.isEmpty()) {
                beginList.add(item);
            }
            else {
                endList.add(item);
            }
        }

        if (targetList.size() <= 1) {
            return null;
        }

        var newItems = new ArrayList<>(beginList);
        newItems.add(combineOptionals(targetList));
        newItems.addAll(endList);
        return ExpressionFactory.sequence(newItems);
    }

    private Expression combineOptionals(List<Optional> optionals) {
        // [a] [b] → [a b | a | b]
        // [a] [b] [c] → [a (b c | b | c) | b c | b | c]
        // [a] [b] [c] [d] → [a  [b (c d|c|d) | c d | c | d]  | b (c d|c|d) | c d | c | d]

        Expression last = null;

        for (var i = optionals.size()-1; i >= 0; i--) {
            var content = optionals.get(i).content;

            if (last == null) {
                last = content;
            }
            else {
                last = combineTwo(content, last);
            }
        }

        if (last == null) {
            throw new IllegalStateException();
        }

        return ExpressionFactory.optional(last);
    }

    private Expression combineTwo(Expression op1, Expression op2) {
        return ExpressionFactory.alternation(
                ExpressionFactory.sequence(op1, op2), op1, op2
        );
    }
}
