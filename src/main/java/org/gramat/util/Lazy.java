package org.gramat.util;

import java.util.function.Supplier;

public class Lazy<T> {

    public static <T> Lazy<T> of(Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }

    private final Supplier<T> supplier;

    private T value;
    private boolean hasValue;

    private Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (hasValue) {
            return value;
        }
        value = supplier.get();
        hasValue = true;
        return value;
    }

    public void flush() {
        value = null;
        hasValue = false;
    }

}
