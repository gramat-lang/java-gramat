package org.gramat.machines;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

public class NodeSet extends LinkedHashSet<Node> {
    public NodeSet() {}

    public NodeSet(Set<Node> nodes) {
        super(nodes);
    }

    public static String id(Set<Node> nodes) {
        var ids = new ArrayList<String>();

        for (var node : nodes) {
            ids.add(String.valueOf(node.id));
        }

        ids.sort(Comparator.naturalOrder());

        return String.join("_", ids);
    }
}
