package org.gramat.runtime;

import org.gramat.inputs.Input;
import org.gramat.util.Require;

public class RAlternation extends RExpression {

    private final RExpression[] items;

    public RAlternation(RExpression[] items) {
        this.items = Require.notEmpty(items);
    }

    @Override
    public void run(Input input, RContext rc) {
        for (var item : items) {
            if (item.test(input.peek())) {
                item.run(input, rc);
                break;
            }
        }
    }

    @Override
    public boolean test(char c) {
        for (var item : items) {
            if (item.test(c)) {
                return true;
            }
        }
        return false;
    }
}
