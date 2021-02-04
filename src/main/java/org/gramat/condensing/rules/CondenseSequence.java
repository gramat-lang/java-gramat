package org.gramat.condensing.rules;

import org.gramat.condensing.CondensingContext;
import org.gramat.condensing.CondensingRule;
import org.gramat.expressions.Expression;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.misc.Nop;
import org.gramat.util.ExpressionList;

public class CondenseSequence extends CondensingRule<Sequence> {
    @Override
    protected Expression process(Sequence seq, CondensingContext cc) {
        if (seq.items.isEmpty()) {
            return new Nop();
        }
        else if (seq.items.size() == 1) {
            return cc.condense(seq.items.get(0));
        }

        return mergeAndCondense(seq, cc);
    }

    private Sequence mergeAndCondense(Sequence seq, CondensingContext cc) {
        var merged = ExpressionList.builder();

        for (var item : seq.items) {
            var newItem = cc.condense(item);

            if (newItem instanceof Sequence) {
                var nested = (Sequence)newItem;

                merged.addAll(nested.items);
            }
            else {
                merged.add(newItem);
            }
        }

        return new Sequence(merged.build());
    }

}
