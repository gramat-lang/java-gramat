package org.gramat.runtime;

import org.gramat.inputs.Input;
import org.gramat.util.Require;

public class RSequence extends RExpression {

    private final RExpression[] items;

    public RSequence(RExpression[] items) {
        this.items = Require.notEmpty(items);
    }

    @Override
    public void run(Input input, RContext rc) {
        for (var item : items) {
            item.run(input, rc);
        }
    }

    @Override
    public boolean test(char c) {
        return items[0].test(c);
    }

}
