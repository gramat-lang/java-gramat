package org.gramat.makers;

import org.gramat.exceptions.GramatException;

public class BooleanMaker implements ValueMaker {
    @Override
    public Object make(String text) {
        if ("true".equalsIgnoreCase(text)) {
            return true;
        }
        else if ("false".equalsIgnoreCase(text)) {
            return false;
        }
        else {
            throw new GramatException("rejected! invalid boolean value");
        }
    }
}
