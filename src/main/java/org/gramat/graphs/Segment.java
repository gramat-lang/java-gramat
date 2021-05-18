package org.gramat.graphs;

import org.gramat.data.nodes.Nodes;

public class Segment {
    public final Node source;
    public final Nodes targets;
    public Segment(Node source, Nodes targets) {
        this.source = source;
        this.targets = targets.copyR();
    }
}
