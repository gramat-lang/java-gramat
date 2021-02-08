package org.gramat.expressions.misc;

import org.gramat.expressions.ExpressionFinal;
import org.gramat.inputs.Location;
import org.gramat.util.Definition;

public class Halt extends ExpressionFinal {
    public Halt(Location begin, Location end) {
        super(begin, end);
    }

    @Override
    protected void define(Definition def) {
        // nothing more
    }
}
