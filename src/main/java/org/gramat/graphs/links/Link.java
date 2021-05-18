package org.gramat.graphs.links;

import org.gramat.graphs.Node;

import java.util.Objects;

public abstract class Link {

    public final Node source;
    public final Node target;

    protected Link(Node source, Node target) {
        this.source = Objects.requireNonNull(source);
        this.target = Objects.requireNonNull(target);
    }
}
