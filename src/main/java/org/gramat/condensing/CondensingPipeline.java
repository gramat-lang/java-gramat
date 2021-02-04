package org.gramat.condensing;

import org.gramat.exceptions.GramatException;
import org.gramat.expressions.Expression;
import org.gramat.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CondensingPipeline {

    private final Logger logger;
    private final List<CondensingRule<?>> rules;
    private final List<Class<?>> resultTypes;

    public CondensingPipeline(Logger logger) {
        this.logger = logger;
        this.rules = new ArrayList<>();
        this.resultTypes = new ArrayList<>();
    }

    public void addRule(CondensingRule<?> rule) {
        rules.add(rule);
    }

    public void addResultType(Class<?> type) {
        resultTypes.add(type);
    }

    public Expression apply(Expression expr, CondensingContext cc) {
        var result = expr;

        for (var rule : rules) {
            if (rule.test(result)) {
                var result0 = result;

                result = rule.apply(result0, cc);

                if (!Objects.equals(result, result0)) {
                    logger.debug("Applied %s to %s", rule.getDescription(), result);
                }
            }
        }

        boolean invalid = true;

        for (var resultType : resultTypes) {
            if (resultType.isInstance(result)) {
                invalid = false;
                break;
            }
        }

        if (invalid) {
            throw new GramatException("invalid pipeline result: " + result);
        }

        return result;
    }

}
