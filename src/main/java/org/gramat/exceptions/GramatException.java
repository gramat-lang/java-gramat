package org.gramat.exceptions;

public class GramatException extends RuntimeException {
    public GramatException(String message) {
        super(message);
    }
    public GramatException(String message, Exception cause) {
        super(message, cause);
    }
}
