package org.gramat.tools;

import org.gramat.errors.ErrorFactory;

import java.util.Collection;

public class Validations {

    public static void notEmpty(Collection<?> value) {
        if (value == null || value.isEmpty()) {
            throw ErrorFactory.invalidEmptyValue();
        }
    }

    public static String notEmpty(String value) {
        if (value == null || value.isEmpty()) {
            throw ErrorFactory.invalidEmptyValue();
        }

        return value;
    }

    private Validations() {}
}
