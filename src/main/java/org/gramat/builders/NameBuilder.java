package org.gramat.builders;

import org.gramat.eval.EvalEngine;
import org.gramat.exceptions.GramatException;

public class NameBuilder implements Builder {
    private String name;

    @Override
    public void accept(Object value) {
        if (name != null) {
            throw new GramatException("error! name already defined");
        }
        else if (value instanceof String) {
            name = (String) value;
        }
        else {
            throw new GramatException("error! not string");
        }
    }

    @Override
    public String build(EvalEngine engine) {
        if (name == null) {
            throw new GramatException("error! missing name");
        }
        return name;
    }
}
