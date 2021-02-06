package org.gramat.exceptions;

import org.gramat.exceptions.reporting.ErrorDetail;
import org.gramat.exceptions.reporting.ErrorReport;

public class GramatException extends RuntimeException {

    private final ErrorDetail errorDetail;

    public GramatException(String message) {
        super(message);
        this.errorDetail = null;
    }
    public GramatException(String message, Exception cause) {
        super(message, cause);
        this.errorDetail = null;
    }

    public GramatException(String message, ErrorDetail errorDetail) {
        super(message);
        this.errorDetail = errorDetail;
    }

    public ErrorDetail getErrorDetail() {
        return errorDetail;
    }
}
