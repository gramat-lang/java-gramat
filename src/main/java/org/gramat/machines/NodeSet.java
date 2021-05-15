package org.gramat.machines;

import java.util.LinkedHashSet;
import java.util.Set;

public class NodeSet extends LinkedHashSet<Node> {
    public NodeSet() {}

    public NodeSet(Set<Node> nodes) {
        super(nodes);
    }
}
