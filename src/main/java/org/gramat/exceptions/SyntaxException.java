package org.gramat.exceptions;

import org.gramat.inputs.Location;

public class SyntaxException extends GramatException {

    public SyntaxException(String message, Location location) {
        super(message + " " + location);
    }

    public SyntaxException(String message, Location begin, Location end) {
        super(message + " " + begin + "," + end);
    }

}
