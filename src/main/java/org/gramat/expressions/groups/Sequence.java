package org.gramat.expressions.groups;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionChildren;
import org.gramat.runtime.RExpression;
import org.gramat.runtime.RSequence;
import org.gramat.util.Definition;
import org.gramat.util.ExpressionList;

public class Sequence extends ExpressionChildren {

    public final ExpressionList items;

    public Sequence(Expression... items) {
        this.items = ExpressionList.of(items);
    }

    public Sequence(ExpressionList items) {
        this.items = items;
    }

    @Override
    public RExpression build() {
        return new RSequence(items.build());
    }

    public void define(Definition def) {
        def.attr("items", items);
    }

    @Override
    public ExpressionList getItems() {
        return items;
    }
}
