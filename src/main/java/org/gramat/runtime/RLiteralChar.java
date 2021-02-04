package org.gramat.runtime;

import org.gramat.exceptions.GramatException;
import org.gramat.inputs.Input;

public class RLiteralChar extends RExpression {

    private final char value;

    public RLiteralChar(char value) {
        this.value = value;
    }

    @Override
    public void run(Input input, RContext rc) {
        var current = input.pull();

        if (current != value) {
            throw new GramatException("not expected char: " + current);
        }
    }

    @Override
    public boolean test(char c) {
        return value == c;
    }
}
