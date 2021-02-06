package org.gramat.expressions.misc;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionFinal;
import org.gramat.inputs.Location;
import org.gramat.util.Definition;

import java.util.Objects;

public class Recursion extends ExpressionFinal {

    public final Expression content;
    public final String name;

    public Recursion(Location begin, Location end, Expression content, String name) {
        super(begin, end);
        this.content = Objects.requireNonNull(content);
        this.name = name;
    }

    @Override
    protected void define(Definition def) {
        def.attr("content", content);
        def.attr("name", name);
    }
}
