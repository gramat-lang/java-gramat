package org.gramat.expressions.groups;

import org.gramat.expressions.ExpressionChildren;
import org.gramat.inputs.Location;
import org.gramat.util.Definition;
import org.gramat.util.ExpressionList;

public class Alternation extends ExpressionChildren {

    public final ExpressionList items;

    public Alternation(Location begin, Location end, ExpressionList items) {
        super(begin, end);
        this.items = items;
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
