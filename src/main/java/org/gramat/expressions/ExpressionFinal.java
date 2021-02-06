package org.gramat.expressions;

import org.gramat.inputs.Location;

import java.util.List;

public abstract class ExpressionFinal extends Expression {

    protected ExpressionFinal(Location begin, Location end) {
        super(begin, end);
    }

    @Override
    public List<Expression> getChildren() {
        return List.of();
    }

}
