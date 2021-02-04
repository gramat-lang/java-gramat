package org.gramat.util;

import org.gramat.exceptions.GramatException;

public class Require {

    private Require() {}

    public static <T> T[] notEmpty(T[] array) {
        if (array == null) {
            throw new GramatException("unexpected null array");
        }
        else if (array.length == 0) {
            throw new GramatException("unexpected empty array");
        }
        return array;
    }

}
