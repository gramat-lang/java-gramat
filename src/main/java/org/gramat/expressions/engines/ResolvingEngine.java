package org.gramat.expressions.engines;

import org.gramat.exceptions.GramatException;
import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionProgram;
import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.groups.Cycle;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.literals.LiteralChar;
import org.gramat.expressions.literals.LiteralRange;
import org.gramat.expressions.literals.LiteralString;
import org.gramat.expressions.misc.ActionExpression;
import org.gramat.expressions.misc.Reference;
import org.gramat.expressions.misc.Wild;
import org.gramat.logging.Logger;
import org.gramat.util.ExpressionMap;

import java.util.Set;

public class ResolvingEngine {

    public static ExpressionProgram resolve(ExpressionProgram original, Logger logger) {
        var recursiveNames = RecursionUtils.findRecursiveNames(original.main, original.dependencies);
        var engine = new ResolvingEngine(logger, recursiveNames, original.dependencies);

        return engine.resolveProgram(original.main);
    }

    private final Logger logger;
    private final ExpressionMap originalRules;
    private final Set<String> recursiveNames;

    private ResolvingEngine(Logger logger, Set<String> recursiveNames, ExpressionMap originalRules) {
        this.logger = logger;
        this.originalRules = originalRules;
        this.recursiveNames = recursiveNames;
    }

    private ExpressionProgram resolveProgram(Expression main) {
        logger.debug("Resolving main expression: %s", main);

        var resolvedMain = resolveExpression(main);
        var resolvedDependencies = new ExpressionMap();

        do {
            for (var recursiveName : recursiveNames) {
                if (!resolvedDependencies.containsKey(recursiveName)) {
                    logger.debug("Resolving dependency: %s", recursiveName);

                    var originalDependency = originalRules.find(recursiveName);
                    var resolvedDependency = resolveExpression(originalDependency);

                    resolvedDependencies.set(recursiveName, resolvedDependency);
                }
            }
        } while (recursiveNames.size() != resolvedDependencies.size());

        return new ExpressionProgram(resolvedMain, resolvedDependencies);
    }

    private Expression resolveExpression(Expression expr) {
        if (expr instanceof LiteralChar
                || expr instanceof LiteralString
                || expr instanceof LiteralRange
                || expr instanceof Wild) {
            return expr;
        }
        else if (expr instanceof Alternation) {
            return resolveAlternation((Alternation) expr);
        }
        else if (expr instanceof Optional) {
            return resolveOptional((Optional) expr);
        }
        else if (expr instanceof Cycle) {
            return resolveCycle((Cycle) expr);
        }
        else if (expr instanceof Sequence) {
            return resolveSequence((Sequence) expr);
        }
        else if (expr instanceof ActionExpression) {
            return resolveAction((ActionExpression) expr);
        }
        else if (expr instanceof Reference) {
            return resolveReference((Reference) expr);
        }
        else {
            throw new GramatException("unsupported value: " + expr);
        }
    }

    private Alternation resolveAlternation(Alternation expr) {
        return new Alternation(expr.beginLocation, expr.endLocation, expr.items.map(this::resolveExpression));
    }

    private Sequence resolveSequence(Sequence expr) {
        return new Sequence(expr.beginLocation, expr.endLocation, expr.items.map(this::resolveExpression));
    }

    private Optional resolveOptional(Optional expr) {
        return new Optional(expr.beginLocation, expr.endLocation, resolveExpression(expr.content));
    }

    private Cycle resolveCycle(Cycle expr) {
        return new Cycle(expr.beginLocation, expr.endLocation, resolveExpression(expr.content));
    }

    private ActionExpression resolveAction(ActionExpression expr) {
        return new ActionExpression(expr.beginLocation, expr.endLocation, expr.scheme, resolveExpression(expr.content), expr.argument);
    }

    private Expression resolveReference(Reference ref) {
        var name = ref.name;

        if (recursiveNames.contains(name)) {
            return ref;
        }

        var originalTarget = originalRules.find(name);

        return resolveExpression(originalTarget);
    }
}
