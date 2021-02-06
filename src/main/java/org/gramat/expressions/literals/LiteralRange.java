package org.gramat.expressions.literals;

import org.gramat.exceptions.GramatException;
import org.gramat.expressions.ExpressionFinal;
import org.gramat.inputs.Location;
import org.gramat.util.Definition;

public class LiteralRange extends ExpressionFinal {

    public final char begin;
    public final char end;

    public LiteralRange(Location beginLocation, Location endLocation, char begin, char end) {
        super(beginLocation, endLocation);
        this.begin = begin;
        this.end = end;

        if (begin >= end) {
            throw new GramatException("invalid char range");
        }
    }

    @Override
    protected void define(Definition def) {
        def.attr("begin", begin);
        def.attr("end", end);
    }
}
