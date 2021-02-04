package org.gramat.expressions.groups;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionChildren;
import org.gramat.runtime.RAlternation;
import org.gramat.runtime.RExpression;
import org.gramat.util.Definition;
import org.gramat.util.ExpressionList;

public class Alternation extends ExpressionChildren {

    public final ExpressionList items;

    public Alternation(ExpressionList items) {
        this.items = items;
    }

    @Override
    public RExpression build() {
        return new RAlternation(items.build());
    }

    @Override
    protected void define(Definition def) {
        def.attr("items", items);
    }

    @Override
    public ExpressionList getItems() {
        return items;
    }
}
