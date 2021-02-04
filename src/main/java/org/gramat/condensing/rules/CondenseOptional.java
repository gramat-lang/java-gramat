package org.gramat.condensing.rules;

import org.gramat.condensing.CondensingContext;
import org.gramat.condensing.CondensingRule;
import org.gramat.expressions.Expression;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Repetition;
import org.gramat.expressions.misc.Nop;

public class CondenseOptional extends CondensingRule<Optional> {
    @Override
    protected Expression process(Optional opt, CondensingContext cc) {
        if (opt.content instanceof Nop) {
            return new Nop();
        }
        else if (opt.content instanceof Optional || opt.content instanceof Repetition) {
            // [ {x} ] → {x} or [ [x] ] → [x]
            return cc.condense(opt.content);
        }
        else {
            var newContent = cc.condense(opt.content);

            return new Optional(newContent);
        }
    }
}
