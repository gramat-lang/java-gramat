package org.gramat.expressions;

public class ExpressionRule {

    public final String name;
    public final Expression expression;

    public ExpressionRule(String name, Expression expression) {
        this.name = name;
        this.expression = expression;
    }
}
