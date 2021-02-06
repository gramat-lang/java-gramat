package org.gramat.expressions;

import org.gramat.inputs.Location;

import java.util.List;

public abstract class ExpressionContent extends Expression {

    protected ExpressionContent(Location begin, Location end) {
        super(begin, end);
    }

    public abstract Expression getContent();

    @Override
    public List<Expression> getChildren() {
        return List.of(getContent());
    }
}
