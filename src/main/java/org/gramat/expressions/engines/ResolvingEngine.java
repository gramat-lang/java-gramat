package org.gramat.expressions.engines;

import org.gramat.exceptions.GramatException;
import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionProgram;
import org.gramat.expressions.actions.ListWrapper;
import org.gramat.expressions.actions.NameWrapper;
import org.gramat.expressions.actions.ObjectWrapper;
import org.gramat.expressions.actions.PropertyWrapper;
import org.gramat.expressions.actions.TextWrapper;
import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Repetition;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.literals.LiteralChar;
import org.gramat.expressions.literals.LiteralRange;
import org.gramat.expressions.literals.LiteralString;
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
        else if (expr instanceof Repetition) {
            return resolveRepetition((Repetition) expr);
        }
        else if (expr instanceof Sequence) {
            return resolveSequence((Sequence) expr);
        }
        else if (expr instanceof ListWrapper) {
            return resolveListWrapper((ListWrapper) expr);
        }
        else if (expr instanceof ObjectWrapper) {
            return resolveObjectWrapper((ObjectWrapper) expr);
        }
        else if (expr instanceof PropertyWrapper) {
            return resolvePropertyWrapper((PropertyWrapper) expr);
        }
        else if (expr instanceof TextWrapper) {
            return resolveTextWrapper((TextWrapper) expr);
        }
        else if (expr instanceof NameWrapper) {
            return resolveNameWrapper((NameWrapper) expr);
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

    private Repetition resolveRepetition(Repetition expr) {
        return new Repetition(expr.beginLocation, expr.endLocation, resolveExpression(expr.content));
    }

    private ListWrapper resolveListWrapper(ListWrapper expr) {
        return new ListWrapper(expr.beginLocation, expr.endLocation, resolveExpression(expr.content), expr.typeHint);
    }

    private ObjectWrapper resolveObjectWrapper(ObjectWrapper expr) {
        return new ObjectWrapper(expr.beginLocation, expr.endLocation, resolveExpression(expr.content), expr.typeHint);
    }

    private PropertyWrapper resolvePropertyWrapper(PropertyWrapper expr) {
        return new PropertyWrapper(expr.beginLocation, expr.endLocation, resolveExpression(expr.content), expr.nameHint);
    }

    private TextWrapper resolveTextWrapper(TextWrapper expr) {
        return new TextWrapper(expr.beginLocation, expr.endLocation, resolveExpression(expr.content), expr.parser);
    }

    private NameWrapper resolveNameWrapper(NameWrapper expr) {
        return new NameWrapper(expr.beginLocation, expr.endLocation, resolveExpression(expr.content));
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
