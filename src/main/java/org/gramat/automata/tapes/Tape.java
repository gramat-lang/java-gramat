package org.gramat.automata.tapes;

import org.gramat.location.Location;

public interface Tape {

    static Tape of(String input, String resource) {
        return new StringTape(input, resource);
    }

    String getResource();

    boolean isOpen();

    char getChar();

    void moveForward();

    Location getLocation();

    int getPosition();
}
