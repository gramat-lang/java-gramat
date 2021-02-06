package org.gramat.builders;

import org.gramat.eval.EvalEngine;
import org.gramat.exceptions.GramatException;

public class MetadataBuilder implements Builder {
    private Object value;
    private boolean defined;

    @Override
    public void acceptMetadata(String name, Object value) {
        throw new GramatException("metdata not supported");
    }

    @Override
    public void acceptContent(Object value) {
        if (defined) {
            throw new GramatException("error! metadata already defined");
        }

        this.value = value;
        this.defined = true;
    }

    @Override
    public Object build(EvalEngine engine) {
        if (!this.defined) {
            throw new GramatException("rejected! missing metadata value");
        }
        return this.value;
    }
}
