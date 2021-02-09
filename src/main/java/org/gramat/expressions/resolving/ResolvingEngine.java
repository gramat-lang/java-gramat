package org.gramat.expressions.resolving;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionProgram;
import org.gramat.expressions.engines.RecursionUtils;
import org.gramat.formatting.ExpressionFormatter;
import org.gramat.logging.Logger;
import org.gramat.util.ExpressionMap;

import java.util.Set;

public interface ResolvingEngine {

    static ExpressionProgram resolve(ExpressionProgram original, Logger logger) {
        var recursiveNames = RecursionUtils.findRecursiveNames(original.main, original.dependencies);

        logger.debug("ResolvingExpression main expression: %s", original.main);

        var resolvedMain = deepResolve(original.main, recursiveNames, original.dependencies, logger);
        var resolvedDependencies = new ExpressionMap();

        do {
            for (var recursiveName : recursiveNames) {
                if (!resolvedDependencies.containsKey(recursiveName)) {
                    logger.debug("ResolvingExpression dependency: %s", recursiveName);

                    var originalDependency = original.dependencies.find(recursiveName);
                    var resolvedDependency = deepResolve(originalDependency, recursiveNames, original.dependencies, logger);

                    resolvedDependencies.set(recursiveName, resolvedDependency);
                }
            }
        } while (recursiveNames.size() != resolvedDependencies.size());

        return new ExpressionProgram(resolvedMain, resolvedDependencies);
    }

    static Expression deepResolve(Expression expr, Set<String> recursiveNames, ExpressionMap dependencies, Logger logger) {
        var result = expr;

        while(true) {
            var rc = new ResolvingContext(logger, recursiveNames, dependencies);

            result = ResolvingExpression.resolve(rc, result);

            var hits = rc.getHits();

            new ExpressionFormatter(System.out).write(result);

            logger.debug("Resolved with %s hit(s)", hits);

            if (hits == 0) {
                break;
            }
        }

        return result;
    }

}
