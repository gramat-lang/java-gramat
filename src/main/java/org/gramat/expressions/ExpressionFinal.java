package org.gramat.expressions;

import java.util.List;

public abstract class ExpressionFinal extends Expression {

    @Override
    public List<Expression> getChildren() {
        return List.of();
    }

}
