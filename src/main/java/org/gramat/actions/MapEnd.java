package org.gramat.actions;

public class MapEnd implements Action {
    public final String typeHint;

    MapEnd(String typeHint) {
        this.typeHint = typeHint;
    }

    @Override
    public String toString() {
        if (typeHint != null) {
            return String.format("map-end(%s)", typeHint);
        }
        return "map-end";
    }
}
