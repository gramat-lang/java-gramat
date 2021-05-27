package org.gramat.automata.evaluation;

import org.gramat.automata.State;
import org.gramat.location.Location;

public class RejectedException extends RuntimeException {

    private final transient Location location;
    private final transient State state;

    public RejectedException(String message, Location location, State state) {
        super(String.format("%s (%s)", message, location));
        this.location = location;
        this.state = state;
    }

    public Location getLocation() {
        return location;
    }

    public State getState() {
        return state;
    }
}
