package org.gramat.condensing.rules;

import org.gramat.condensing.CondensingContext;
import org.gramat.condensing.CondensingRule;
import org.gramat.expressions.Expression;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.literals.LiteralChar;
import org.gramat.expressions.literals.LiteralString;
import org.gramat.expressions.misc.Nop;
import org.gramat.util.ExpressionList;

public class CondenseLiteralString extends CondensingRule<LiteralString> {

    @Override
    public Expression process(LiteralString lit, CondensingContext cc) {
        if (lit.value.isEmpty()) {
            return new Nop();
        }

        var items = ExpressionList.builder();

        for (var c : lit.value.toCharArray()) {
            items.add(new LiteralChar(c));
        }

        return new Sequence(items.build());
    }

}
