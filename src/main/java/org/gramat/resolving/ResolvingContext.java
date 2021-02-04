package org.gramat.resolving;

import org.gramat.util.ExpressionMap;

import java.util.LinkedHashSet;
import java.util.Set;

public class ResolvingContext {

    public final ExpressionMap rules;
    public final Set<String> dependencies;

    public ResolvingContext(ExpressionMap rules) {
        this.rules = rules;
        dependencies = new LinkedHashSet<>();
    }

}
