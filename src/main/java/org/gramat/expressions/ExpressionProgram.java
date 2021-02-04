package org.gramat.expressions;

import org.gramat.util.ExpressionMap;

public class ExpressionProgram {

    public final Expression main;
    public final ExpressionMap dependencies;


    public ExpressionProgram(Expression main, ExpressionMap dependencies) {
        this.main = main;
        this.dependencies = dependencies;
    }
}
