package org.gramat.location;

public class LocationPoint {

    public final int line;
    public final int column;

    public LocationPoint(int line, int column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", line, column);
    }
}
