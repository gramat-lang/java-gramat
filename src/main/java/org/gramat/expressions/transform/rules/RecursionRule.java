package org.gramat.expressions.transform.rules;

import org.gramat.expressions.Expression;
import org.gramat.expressions.misc.Reference;
import org.gramat.expressions.transform.TransformRule;
import org.gramat.util.ExpressionMap;

import java.util.Set;

public class RecursionRule extends TransformRule {

    private final Set<String> recursiveNames;
    private final ExpressionMap dependencies;

    public RecursionRule(Set<String> recursiveNames, ExpressionMap dependencies) {
        this.recursiveNames = recursiveNames;
        this.dependencies = dependencies;
    }

    @Override
    protected Expression tryReference(Reference reference) {
        var name = reference.name;

        if (recursiveNames.contains(name)) {
            return null;
        }

        return dependencies.find(name);
    }
}
