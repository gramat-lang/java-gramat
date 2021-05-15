package org.gramat.errors;

import org.gramat.location.Location;

public class ErrorFactory {

    public static RuntimeException keyAlreadyExists(String key) {
        return new RuntimeException("key already exists: " + key);
    }

    public static RuntimeException syntaxError(Location location, String format, Object... args) {
        return new RuntimeException(String.format(format, args) + " @ " + location);
    }

    public static RuntimeException invalidEmptyValue() {
        return new RuntimeException("invalid empty value");
    }

    public static RuntimeException internalError(String message) {
        return new RuntimeException(message);
    }

    public static RuntimeException internalError(Exception cause) {
        return new RuntimeException(cause);
    }

    public static RuntimeException notFound(String message) {
        return new RuntimeException(message);
    }

    public static RuntimeException notImplemented() {
        return new UnsupportedOperationException();
    }

    private ErrorFactory() {}
}
