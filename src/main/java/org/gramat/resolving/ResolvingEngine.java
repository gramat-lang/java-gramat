package org.gramat.resolving;

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

public class ResolvingEngine {

    private final Logger logger;

    public ResolvingEngine(Logger logger) {
        this.logger = logger;
    }

    public ExpressionProgram resolve(ExpressionProgram original) {
        var rc = new ResolvingContext(original.dependencies);

        logger.debug("Resolving main expression: %s", original.main);

        var resolvedMain = resolveExpression(original.main, rc);
        var resolvedDependencies = new ExpressionMap();

        do {
            for (var dependencyName : rc.dependencies) {
                if (!resolvedDependencies.containsKey(dependencyName)) {
                    logger.debug("Resolving dependency: %s", dependencyName);

                    var originalDependency = original.dependencies.find(dependencyName);
                    var resolvedDependency = resolveExpression(originalDependency, rc);

                    resolvedDependencies.set(dependencyName, resolvedDependency);
                }
            }
        } while (rc.dependencies.size() != resolvedDependencies.size());

        return new ExpressionProgram(resolvedMain, resolvedDependencies);
    }

    private Expression resolveExpression(Expression expr, ResolvingContext rc) {
        if (expr instanceof LiteralChar
                || expr instanceof LiteralString
                || expr instanceof LiteralRange
                || expr instanceof Wild) {
            return expr;
        } else if (expr instanceof Alternation) {
            return resolveAlternation((Alternation) expr, rc);
        } else if (expr instanceof Optional) {
            return resolveOptional((Optional) expr, rc);
        } else if (expr instanceof Repetition) {
            return resolveRepetition((Repetition) expr, rc);
        } else if (expr instanceof Sequence) {
            return resolveSequence((Sequence) expr, rc);
        } else if (expr instanceof ListWrapper) {
            return resolveListWrapper((ListWrapper) expr, rc);
        } else if (expr instanceof ObjectWrapper) {
            return resolveObjectWrapper((ObjectWrapper) expr, rc);
        } else if (expr instanceof PropertyWrapper) {
            return resolvePropertyWrapper((PropertyWrapper) expr, rc);
        } else if (expr instanceof TextWrapper) {
            return resolveTextWrapper((TextWrapper) expr, rc);
        } else if (expr instanceof NameWrapper) {
            return resolveNameWrapper((NameWrapper) expr, rc);
        } else if (expr instanceof Reference) {
            return resolveReference((Reference) expr, rc);
        } else {
            throw new GramatException("unsupported value: " + expr);
        }
    }

    private Alternation resolveAlternation(Alternation expr, ResolvingContext rc) {
        return new Alternation(expr.items.map(item -> resolveExpression(item, rc)));
    }

    private Sequence resolveSequence(Sequence expr, ResolvingContext rc) {
        return new Sequence(expr.items.map(item -> resolveExpression(item, rc)));
    }

    private Optional resolveOptional(Optional expr, ResolvingContext rc) {
        return new Optional(resolveExpression(expr.content, rc));
    }

    private Repetition resolveRepetition(Repetition expr, ResolvingContext rc) {
        return new Repetition(resolveExpression(expr.content, rc));
    }

    private ListWrapper resolveListWrapper(ListWrapper expr, ResolvingContext rc) {
        return new ListWrapper(resolveExpression(expr.content, rc), expr.typeHint);
    }

    private ObjectWrapper resolveObjectWrapper(ObjectWrapper expr, ResolvingContext rc) {
        return new ObjectWrapper(resolveExpression(expr.content, rc), expr.typeHint);
    }

    private PropertyWrapper resolvePropertyWrapper(PropertyWrapper expr, ResolvingContext rc) {
        return new PropertyWrapper(resolveExpression(expr.content, rc), expr.nameHint);
    }

    private TextWrapper resolveTextWrapper(TextWrapper expr, ResolvingContext rc) {
        return new TextWrapper(resolveExpression(expr.content, rc), expr.parser);
    }

    private NameWrapper resolveNameWrapper(NameWrapper expr, ResolvingContext rc) {
        return new NameWrapper(resolveExpression(expr.content, rc));
    }

    private Expression resolveReference(Reference ref, ResolvingContext rc) {
        var name = ref.name;

        if (rc.dependencies.contains(name)) {
            return ref;
        }

        var originalTarget = rc.rules.find(name);

        if (RecursionUtils.isRecursive(originalTarget, name, rc.rules)) {
            logger.debug("Detected dependency: %s", name);
            rc.dependencies.add(name);
            return ref;
        }

        return resolveExpression(originalTarget, rc);
    }
}
