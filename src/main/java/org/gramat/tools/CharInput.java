package org.gramat.tools;

import org.gramat.errors.ErrorFactory;
import org.gramat.location.Location;
import org.gramat.location.LocationBuilder;
import org.gramat.location.LocationPoint;

public class CharInput {

    public static CharInput of(String content, String resource) {
        return new CharInput(content.toCharArray(), resource);
    }

    private final String resource;
    private final char[] content;

    private int position;

    private CharInput(char[] content, String resource) {
        this.content = content;
        this.resource = resource;
        this.position = 0;
    }

    public boolean isAlive() {
        return position < content.length;
    }

    public char peek() {
        if (position < content.length) {
            return content[position];
        }
        return '\0';
    }

    public void move() {
        if (position < content.length) {
            position++;
        }
        else {
            throw ErrorFactory.syntaxError(getLocation(), "Unexpected end of file");
        }
    }

    public char pull() {
        if (position < content.length) {
            var symbol = content[position];

            position++;

            return symbol;
        }
        else {
            throw ErrorFactory.syntaxError(getLocation(), "Unexpected end of file");
        }
    }

    public boolean pull(String token) {
        var length = token.length();

        if (length == 0) {
            throw ErrorFactory.internalError("Unexpected empty token");
        }

        for (var i = 0; i < length; i++) {
            var p = position + i;

            if (p < content.length && content[p] != token.charAt(i)) {
                return false;
            }
        }

        position += length;
        return true;
    }

    public boolean pull(char expected) {
        if (position < content.length) {
            if (expected != content[position]) {
                return false;
            }

            position++;

            return true;
        }

        return false;
    }

    public LocationBuilder beginLocation() {
        return Location.builder()
                .resource(resource)
                .begin(computeLocationPoint(position));
    }

    public void endLocation(LocationBuilder builder) {
        builder.end(computeLocationPoint(position));
    }

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
            if (content[i] == '\n') {
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
