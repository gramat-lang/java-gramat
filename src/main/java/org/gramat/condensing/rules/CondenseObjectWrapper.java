package org.gramat.condensing.rules;

import org.gramat.condensing.CondensingContext;
import org.gramat.condensing.CondensingRule;
import org.gramat.expressions.Expression;
import org.gramat.expressions.actions.ObjectBegin;
import org.gramat.expressions.actions.ObjectEnd;
import org.gramat.expressions.actions.ObjectWrapper;
import org.gramat.expressions.groups.Sequence;

public class CondenseObjectWrapper extends CondensingRule<ObjectWrapper> {
    @Override
    protected Expression process(ObjectWrapper obj, CondensingContext cc) {
        var begin = new ObjectBegin();
        var end = new ObjectEnd(obj.typeHint);
        var newContent = cc.condense(obj.content);
        return new Sequence(begin, newContent, end);
    }
}
