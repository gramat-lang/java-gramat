package org.gramat.graphs;

import org.gramat.data.nodes.Nodes;

public class Segment {
    public final Node source;
    public final Node target;
    public Segment(Node source, Node target) {
        this.source = source;
        this.target = target;
    }
}
