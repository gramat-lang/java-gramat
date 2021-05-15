package org.gramat.machines;

import java.util.Set;

public class Segment {

    public final Node source;
    public final Set<Node> targets;

    public Segment(Node source, Set<Node> targets) {
        this.source = source;
        this.targets = targets;
    }
}
