package org.gramat.graphs.links;

import org.gramat.graphs.Node;

public class LinkEmpty extends Link {

    public LinkEmpty(Node source, Node target) {
        super(source, target);
    }

    @Override
    public String toString() {
        return String.format("Link{%s -> %s : empty}", source, target);
    }

}
