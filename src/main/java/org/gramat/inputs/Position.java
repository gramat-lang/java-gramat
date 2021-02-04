package org.gramat.inputs;

import java.util.Objects;

public class Position {

    private final String resource;
    private final int offset;
    private final int line;
    private final int column;

    public Position(String resource, int offset, int line, int column) {
        this.resource = resource;
        this.offset = offset;
        this.line = line;
        this.column = column;
    }

    public String getResource() {
        return resource;
    }

    public int getOffset() {
        return offset;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();

        if (resource != null) {
            builder.append(resource);
        }

        builder.append("@");
        builder.append(line);
        builder.append(':');
        builder.append(column);

        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        else if (o == null || getClass() != o.getClass()) { return false; }
        Position that = (Position) o;
        return this.offset == that.offset
                && this.line == that.line
                && this.column == that.column
                && Objects.equals(this.resource, that.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resource, offset, line, column);
    }
}
