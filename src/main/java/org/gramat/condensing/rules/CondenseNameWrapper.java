package org.gramat.condensing.rules;

import org.gramat.condensing.CondensingContext;
import org.gramat.condensing.CondensingRule;
import org.gramat.expressions.Expression;
import org.gramat.expressions.actions.NameBegin;
import org.gramat.expressions.actions.NameEnd;
import org.gramat.expressions.actions.NameWrapper;
import org.gramat.expressions.groups.Sequence;

public class CondenseNameWrapper extends CondensingRule<NameWrapper> {
    @Override
    protected Expression process(NameWrapper nme, CondensingContext cc) {
        var begin = new NameBegin();
        var end = new NameEnd();
        var newContent = cc.condense(nme.content);
        return new Sequence(begin, newContent, end);
    }
}
