package org.gramat.condensing.rules;

import org.gramat.condensing.CondensingContext;
import org.gramat.condensing.CondensingRule;
import org.gramat.expressions.Expression;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Repetition;
import org.gramat.expressions.misc.Nop;

public class CondenseRepetition extends CondensingRule<Repetition> {
    @Override
    protected Expression process(Repetition rep, CondensingContext cc) {
        if (rep.content instanceof Nop) {
            return rep.content;
        }
        else if (rep.content instanceof Repetition || rep.content instanceof Optional) {
            // { {x} } → {x} or { [x] } → [x]
            return cc.condense(rep.content);
        }
        else {
            var newContent = cc.condense(rep.content);

            return new Repetition(newContent);
        }
    }
}
