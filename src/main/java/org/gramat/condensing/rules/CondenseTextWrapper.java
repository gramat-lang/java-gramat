package org.gramat.condensing.rules;

import org.gramat.condensing.CondensingContext;
import org.gramat.condensing.CondensingRule;
import org.gramat.expressions.Expression;
import org.gramat.expressions.actions.TextBegin;
import org.gramat.expressions.actions.TextEnd;
import org.gramat.expressions.actions.TextWrapper;
import org.gramat.expressions.groups.Sequence;

public class CondenseTextWrapper extends CondensingRule<TextWrapper> {
    @Override
    protected Expression process(TextWrapper txt, CondensingContext cc) {
        var begin = new TextBegin();
        var end = new TextEnd(txt.parser);
        var newContent = cc.condense(txt.content);
        return new Sequence(begin, newContent, end);
    }
}
