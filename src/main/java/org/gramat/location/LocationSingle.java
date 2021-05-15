package org.gramat.location;

import java.util.List;

public class LocationSingle implements Location {
    private final String resource;
    private final LocationPoint point;

    LocationSingle(String resource, LocationPoint point) {
        this.resource = resource;
        this.point = point;
    }

    @Override
    public String getResource() {
        return resource;
    }

    @Override
    public List<LocationPoint> getPoints() {
        return List.of(point);
    }

    @Override
    public String toString() {
        if (resource == null) {
            return String.format("@ %s", point);
        }
        return String.format("%s @ %s", resource, point);
    }
}
