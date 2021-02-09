package org.gramat.expressions.resolving;

import org.gramat.logging.Logger;
import org.gramat.util.ExpressionMap;

import java.util.Set;

public class ResolvingContext {
    public final Logger logger;
    public final ExpressionMap originalRules;
    public final Set<String> recursiveNames;

    private int hits;

    public ResolvingContext(Logger logger, Set<String> recursiveNames, ExpressionMap originalRules) {
        this.logger = logger;
        this.originalRules = originalRules;
        this.recursiveNames = recursiveNames;
    }

    public void hit(String description) {
        logger.debug("Resolved: " + description);
        hits++;
    }

    public int getHits() {
        return hits;
    }
}
