package org.gramat.expressions.resolving;

import org.gramat.expressions.ExpressionProgram;
import org.gramat.expressions.engines.RecursionUtils;
import org.gramat.logging.Logger;

public interface ResolvingEngine {

    static ExpressionProgram resolve(ExpressionProgram original, Logger logger) {
        var recursiveNames = RecursionUtils.findRecursiveNames(original.main, original.dependencies);
        var rc = new ResolvingContext(logger, recursiveNames, original.dependencies);

        return ResolvingProgram.resolve(rc, original.main);
    }

}
