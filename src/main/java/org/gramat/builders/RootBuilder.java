package org.gramat.builders;

import org.gramat.eval.EvalEngine;
import org.gramat.exceptions.GramatException;

public class RootBuilder implements Builder {
    private Object value;
    private boolean defined;

    @Override
    public void acceptMetadata(String name, Object value) {
        throw new GramatException("unsupported metadata: " + name);
    }

    @Override
    public void acceptContent(Object value) {
        if (this.defined) {
            throw new GramatException("rejected! too much root values");
        }

        this.value = value;
        this.defined = true;
    }

    @Override
    public Object build(EvalEngine engine) {
        return value;
    }
}
