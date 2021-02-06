package org.gramat.builders;

import org.gramat.eval.EvalEngine;
import org.gramat.exceptions.GramatException;

public class PropertyBuilder implements Builder {
    private String name;
    private Object value;
    private boolean defined;

    public String getName() {
        if (name == null) {
            throw new GramatException("rejected! missing name");
        }
        return name;
    }

    public void acceptName(String name) {
        // TODO validate override
        this.name = name;
    }

    @Override
    public void acceptMetadata(String metaName, Object metaValue) {
        if ("name".equals(metaName)) {
            acceptName((String)metaValue); // TODO improve string conversion
        }
        else {
            throw new GramatException("unsupported metadata: " + metaName);
        }
    }

    @Override
    public void acceptContent(Object value) {
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
