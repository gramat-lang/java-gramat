package org.gramat.expressions;

import org.gramat.inputs.Location;
import org.gramat.util.ExpressionList;

import java.util.List;

public abstract class ExpressionChildren extends Expression {

    protected ExpressionChildren(Location begin, Location end) {
        super(begin, end);
    }

    public abstract ExpressionList getItems();

    @Override
    public List<Expression> getChildren() {
        return getItems();
    }
}
