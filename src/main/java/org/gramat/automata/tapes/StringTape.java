package org.gramat.automata.tapes;

import org.gramat.location.Location;
import org.gramat.location.LocationPoint;

public class StringTape implements Tape {

    private final String content;
    private final String resource;
    private final int length;

    private int position;

    public StringTape(String content, String resource) {
        this.content = content;
        this.resource = resource;
        this.length = content.length();
    }

    @Override
    public String getResource() {
        return resource;
    }

    @Override
    public boolean isOpen() {
        return position < length;
    }

    @Override
    public char getChar() {
        if (position >= length) {
            throw new RuntimeException();
        }
        return content.charAt(position);
    }

    @Override
    public void moveForward() {
        position++;
    }

    @Override
    public Location getLocation() {
        return Location.builder()
                .resource(resource)
                .begin(computeLocationPoint(position))
                .build();
    }

    private LocationPoint computeLocationPoint(int position) {
        var line = 1;
        var column = 1;

        for (var i = 0; i < position; i++) {
            if (content.charAt(i) == '\n') {
                line++;
                column = 1;
            }
            else {
                column++;
            }
        }

        return new LocationPoint(line, column);
    }


}
