package org.gramat.location;

import java.util.List;

public class LocationEmpty implements Location {
    private final String resource;

    LocationEmpty(String resource) {
        this.resource = resource;
    }

    @Override
    public String getResource() {
        return resource;
    }

    @Override
    public List<LocationPoint> getPoints() {
        return List.of();
    }

    @Override
    public String toString() {
        if (resource == null) {
            return "@ unknown location";
        }
        return String.format("%s @ unknown location", resource);
    }
}
