package org.gramat.expressions;

import org.gramat.tools.DataUtils;

import java.util.Map;

public class ExpressionProgram {

    public final Expression main;
    public final Map<String, Expression> dependencies;

    public ExpressionProgram(Expression main, Map<String, Expression> dependencies) {
        this.main = main;
        this.dependencies = DataUtils.immutableCopy(dependencies);
    }
}
