package org.gramat.location;

import java.util.List;

public class LocationRange implements Location {
    private final String resource;
    private final LocationPoint begin;
    private final LocationPoint end;

    LocationRange(String resource, LocationPoint begin, LocationPoint end) {
        this.resource = resource;
        this.begin = begin;
        this.end = end;
    }

    @Override
    public String getResource() {
        return resource;
    }

    @Override
    public List<LocationPoint> getPoints() {
        return List.of(begin, end);
    }

    @Override
    public String toString() {
        if (resource == null) {
            return String.format("@ %s-%s", begin, end);
        }
        return String.format("%s @ %s-%s", resource, begin, end);
    }
}
