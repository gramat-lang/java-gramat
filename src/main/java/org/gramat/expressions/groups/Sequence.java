package org.gramat.expressions.groups;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionChildren;
import org.gramat.inputs.Location;
import org.gramat.util.Definition;
import org.gramat.util.ExpressionList;

public class Sequence extends ExpressionChildren {

    public final ExpressionList items;

    public Sequence(Location begin, Location end, Expression... items) {
        super(begin, end);
        this.items = ExpressionList.of(items);
    }

    public Sequence(Location begin, Location end, ExpressionList items) {
        super(begin, end);
        this.items = items;
    }

    public void define(Definition def) {
        def.attr("items", items);
    }

    @Override
    public ExpressionList getItems() {
        return items;
    }
}
