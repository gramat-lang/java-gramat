package org.gramat.inputs;

import java.util.Objects;

public class Location {

    private final String resource;
    private final Position beginPosition;
    private final Position endPosition;

    public Location(Position position) {
        this(position, position);
    }

    public Location(Position beginPosition, Position endPosition) {
        if (!Objects.equals(beginPosition.getResource(), endPosition.getResource())) {
            throw new RuntimeException();
        }
        this.resource = beginPosition.getResource();
        this.beginPosition = beginPosition;
        this.endPosition = endPosition;
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();

        if (resource != null) {
            builder.append(resource);
        }

        builder.append("@");

        builder.append(beginPosition.getLine());
        builder.append(':');

        if (beginPosition.getLine() != endPosition.getLine()) {
            builder.append(beginPosition.getColumn());
            builder.append('-');
            builder.append(endPosition.getLine());
            builder.append(':');
            builder.append(endPosition.getColumn());
        }
        else if (beginPosition.getColumn() == endPosition.getColumn()) {
            builder.append(beginPosition.getColumn());
        }
        else {
            builder.append(beginPosition.getColumn());
            builder.append('-');
            builder.append(endPosition.getColumn());
        }

        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        else if (o == null || getClass() != o.getClass()) { return false; }
        Location that = (Location) o;
        return Objects.equals(this.resource, that.resource)
                && Objects.equals(this.beginPosition, that.beginPosition)
                && Objects.equals(this.endPosition, that.endPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resource, beginPosition, endPosition);
    }
}
