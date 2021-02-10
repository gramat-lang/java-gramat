package org.gramat.expressions.transform;

import org.gramat.expressions.Expression;
import org.gramat.logging.Logger;
import org.gramat.util.PP;

public class TransformContext {
    private final Logger logger;

    private int changes;

    public TransformContext(Logger logger) {
        this.logger = logger;
    }

    public int getChanges() {
        return changes;
    }

    public void track(TransformRule rule, Expression oldExpr, Expression newExpr) {
        changes++;

        logger.debug("%s: %s → %s", rule.getClass().getSimpleName(), PP.str(oldExpr), PP.str(newExpr));
    }
}
