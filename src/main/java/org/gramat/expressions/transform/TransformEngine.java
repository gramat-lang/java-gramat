package org.gramat.expressions.transform;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionProgram;
import org.gramat.expressions.engines.RecursionUtils;
import org.gramat.expressions.transform.rules.CombinationRule;
import org.gramat.expressions.transform.rules.DeterministicRule;
import org.gramat.expressions.transform.rules.FlatteningRule;
import org.gramat.expressions.transform.rules.PromotionRule;
import org.gramat.expressions.transform.rules.RecursionRule;
import org.gramat.expressions.transform.rules.ReductionRule;
import org.gramat.logging.Logger;
import org.gramat.util.ExpressionMap;
import org.gramat.util.PP;

import java.util.Set;

public interface TransformEngine {

    static ExpressionProgram run(ExpressionProgram original, Logger logger) {
        var recursiveNames = RecursionUtils.findRecursiveNames(original.main, original.dependencies);

        logger.debug("Transforming main: %s", PP.str(original.main));

        var rules = createRules(recursiveNames, original.dependencies);
        var resolvedMain = apply(rules, original.main, logger);
        var resolvedDependencies = new ExpressionMap();

        do {
            for (var recursiveName : recursiveNames) {
                if (!resolvedDependencies.containsKey(recursiveName)) {
                    logger.debug("Transforming dependency: %s", recursiveName);

                    var originalDependency = original.dependencies.find(recursiveName);
                    var resolvedDependency = apply(rules, originalDependency, logger);

                    resolvedDependencies.set(recursiveName, resolvedDependency);
                }
            }
        } while (recursiveNames.size() != resolvedDependencies.size());

        return new ExpressionProgram(resolvedMain, resolvedDependencies);
    }

    private static Expression apply(TransformRule[] rules, Expression expression, Logger logger) {
        var result = expression;
        var ctx = new TransformContext(logger);

        while (true) {
            var result0 = result;

            for (var rule : rules) {
                result = rule.transform(ctx, result);
            }

            if (result0 == result) {
                break;
            }
        }

        logger.debug("Transformation finished with %s change(s).", ctx.getChanges());

        return result;
    }

    private static TransformRule[] createRules(Set<String> recursiveNames, ExpressionMap dependencies) {
        return new TransformRule[] {
                new ReductionRule(),
                new PromotionRule(),
                new FlatteningRule(),
                new RecursionRule(recursiveNames, dependencies),
                new CombinationRule(),
                new DeterministicRule(),
        };
    }

}
