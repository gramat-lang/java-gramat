package org.gramat.graphs.links;

import org.gramat.graphs.Node;

import java.util.Set;

public class LinkEmpty extends Link {

    public LinkEmpty(String ids, Node source, Node target) {
        super(ids, source, target);
    }

    @Override
    public String toString() {
        return String.format("Link{%s -> %s : empty}", source, target);
    }

}
