package org.gramat.condensing.rules;

import org.gramat.condensing.CondensingContext;
import org.gramat.condensing.CondensingRule;
import org.gramat.expressions.Expression;
import org.gramat.expressions.actions.PropertyBegin;
import org.gramat.expressions.actions.PropertyEnd;
import org.gramat.expressions.actions.PropertyWrapper;
import org.gramat.expressions.groups.Sequence;

public class CondensePropertyWrapper extends CondensingRule<PropertyWrapper> {
    @Override
    protected Expression process(PropertyWrapper pty, CondensingContext cc) {
        var begin = new PropertyBegin();
        var end = new PropertyEnd(pty.nameHint);
        var newContent = cc.condense(pty.content);
        return new Sequence(begin, newContent, end);
    }
}
