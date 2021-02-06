package org.gramat.tracking;

import org.gramat.inputs.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SourceMap {

    private final Map<Integer, Set<Location>> nodesMap;
    private final Map<Integer, Set<Location>> actionMap;

    public SourceMap() {
        nodesMap = new HashMap<>();
        actionMap = new HashMap<>();
    }

    public void addNodeLocations(int id, Set<Location> locations) {
        nodesMap.put(id, locations);  // TODO merge locations
    }

    public void addActionLocations(int id, Set<Location> locations) {
        actionMap.put(id, locations);  // TODO merge locations
    }

    public Set<Location> getNodeLocations(int id) {
        return nodesMap.get(id);
    }

    public Set<Location> getActionLocations(int id) {
        return actionMap.get(id);
    }
}
