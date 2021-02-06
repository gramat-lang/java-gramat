package org.gramat.builders;

import org.gramat.eval.EvalEngine;
import org.gramat.exceptions.GramatException;

public class PropertyBuilder implements Builder {
    private String name;
    private Object value;
    private boolean defined;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        if (name == null) {
            throw new GramatException("rejected! missing name");
        }
        return name;
    }

    @Override
    public void accept(Object value) {
        if (this.defined) {
            throw new GramatException("rejected! too much property values");
        }

        this.value = value;
        this.defined = true;
    }

    @Override
    public Object build(EvalEngine engine) {
        if (!this.defined) {
            throw new GramatException("rejected! missing property value");
        }
        return this.value;
    }
}
