package org.gramat.expressions.resolving;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionProgram;
import org.gramat.util.ExpressionMap;

public interface ResolvingProgram {
    static ExpressionProgram resolve(ResolvingContext rc, Expression main) {
        rc.logger.debug("ResolvingExpression main expression: %s", main);

        var resolvedMain = ResolvingExpression.resolve(rc, main);
        var resolvedDependencies = new ExpressionMap();

        do {
            for (var recursiveName : rc.recursiveNames) {
                if (!resolvedDependencies.containsKey(recursiveName)) {
                    rc.logger.debug("ResolvingExpression dependency: %s", recursiveName);

                    var originalDependency = rc.originalRules.find(recursiveName);
                    var resolvedDependency = ResolvingExpression.resolve(rc, originalDependency);

                    resolvedDependencies.set(recursiveName, resolvedDependency);
                }
            }
        } while (rc.recursiveNames.size() != resolvedDependencies.size());

        return new ExpressionProgram(resolvedMain, resolvedDependencies);
    }
}
