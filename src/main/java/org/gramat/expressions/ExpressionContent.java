package org.gramat.expressions;

import java.util.List;

public abstract class ExpressionContent extends Expression {

    public abstract Expression getContent();

    @Override
    public List<Expression> getChildren() {
        return List.of(getContent());
    }
}
