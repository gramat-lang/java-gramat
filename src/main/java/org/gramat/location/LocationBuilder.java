package org.gramat.location;

public class LocationBuilder {

    private String resource;
    private LocationPoint begin;
    private LocationPoint end;

    public Location build() {
        if (begin != null) {
            if (end != null) {
                return new LocationRange(resource, begin, end);
            }
            return new LocationSingle(resource, begin);
        }
        else if (end == null) {
            return new LocationEmpty(resource);
        }
        else {
            return new LocationSingle(resource, end);
        }
    }

    public LocationBuilder resource(String resource) {
        this.resource = resource;
        return this;
    }

    public LocationBuilder begin(LocationPoint point) {
        this.begin = point;
        return this;
    }

    public LocationBuilder end(LocationPoint point) {
        this.end = point;
        return this;
    }
}
