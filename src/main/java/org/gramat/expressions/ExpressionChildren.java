package org.gramat.expressions;

import org.gramat.util.ExpressionList;

import java.util.List;

public abstract class ExpressionChildren extends Expression {

    public abstract ExpressionList getItems();

    @Override
    public List<Expression> getChildren() {
        return getItems();
    }
}
