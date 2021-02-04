package org.gramat.exceptions;

import org.gramat.inputs.Location;
import org.gramat.inputs.Position;

public class SyntaxException extends GramatException {

    public SyntaxException(String message, Location location) {
        super(message + " " + location);
    }

    public SyntaxException(String message, Position position) {
        super(message + " " + position);
    }

    public SyntaxException(String message, Position begin, Position end) {
        this(message, new Location(begin, end));
    }

}
