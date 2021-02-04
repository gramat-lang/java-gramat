package org.gramat.runtime;

import org.gramat.exceptions.GramatException;
import org.gramat.inputs.Input;
import org.gramat.inputs.InputCharSequence;

public abstract class RExpression {

    public abstract void run(Input input, RContext rc);

    public abstract boolean test(char c);

    public void eval(CharSequence input) {
        eval(new InputCharSequence(input));
    }

    public void eval(Input input) {
        var rc = new RContext();

        run(input, rc);

        if (input.alive()) {
            throw new GramatException("unexpected content at " + input.position());
        }
    }
}
