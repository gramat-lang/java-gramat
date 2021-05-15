package org.gramat.actions;

public class MapBegin implements Action {

    static final MapBegin INSTANCE = new MapBegin();

    private MapBegin() {}

    @Override
    public String toString() {
        return "map-begin";
    }
}
