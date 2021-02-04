package org.gramat.condensing;

import org.gramat.expressions.Expression;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class CondensingRule<T extends Expression> {

    protected abstract Expression process(T expr, CondensingContext cc);

    private final Class<T> expressionType;

    protected CondensingRule() {
        expressionType = extractExpressionType(getClass().getGenericSuperclass());
    }

    @SuppressWarnings("unchecked")
    private Class<T> extractExpressionType(Type superClass) {
        var paramType = (ParameterizedType)superClass;
        var typeArgs = paramType.getActualTypeArguments();
        return (Class<T>)typeArgs[0];
    }

    public String getDescription() {
        return getClass().getSimpleName();  // TODO transform: "CamelCase" -> "Camel case"
    }

    public boolean test(Expression expr) {
        return expressionType.isInstance(expr);
    }

    public Expression apply(Expression expr, CondensingContext cc) {
        if (expressionType.isInstance(expr)) {
            return process(expressionType.cast(expr), cc);
        }
        return null;
    }

}
