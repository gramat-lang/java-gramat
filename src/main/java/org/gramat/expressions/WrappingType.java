package org.gramat.expressions;

import java.util.Optional;

public enum WrappingType {
    MAP,
    LIST,
    VALUE,
    KEY,
    PUT,
    ;

    public static Optional<WrappingType> parse(String value) {
        if ("map".equals(value)) {
            return Optional.of(MAP);
        }
        else if ("list".equals(value)) {
            return Optional.of(LIST);
        }
        else if ("value".equals(value)) {
            return Optional.of(VALUE);
        }
        else if ("key".equals(value)) {
            return Optional.of(KEY);
        }
        else if ("put".equals(value)) {
            return Optional.of(PUT);
        }
        else {
            return Optional.empty();
        }
    }
}
