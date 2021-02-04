package org.gramat.condensing.rules;

import org.gramat.condensing.CondensingContext;
import org.gramat.condensing.CondensingRule;
import org.gramat.expressions.Expression;
import org.gramat.expressions.actions.ObjectBegin;
import org.gramat.expressions.actions.ObjectEnd;
import org.gramat.expressions.actions.ListWrapper;
import org.gramat.expressions.groups.Sequence;

public class CondenseListWrapper extends CondensingRule<ListWrapper> {
    @Override
    protected Expression process(ListWrapper lst, CondensingContext cc) {
        var begin = new ObjectBegin();
        var end = new ObjectEnd(lst.typeHint);
        var newContent = cc.condense(lst.content);
        return new Sequence(begin, newContent, end);
    }
}
