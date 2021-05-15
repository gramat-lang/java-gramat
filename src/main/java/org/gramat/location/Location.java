package org.gramat.location;

import java.util.List;

public interface Location {

    static LocationBuilder builder() {
        return new LocationBuilder();
    }

    String getResource();

    List<LocationPoint> getPoints();

}
