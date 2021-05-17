package org.gramat.data;

import org.gramat.graphs.Node;
import org.gramat.tools.DataUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class NodesR implements Nodes {

    private final LinkedHashSet<Node> nodes;
    private final String id;

    public NodesR(Collection<Node> nodes) {
        this.nodes = new LinkedHashSet<>(nodes);
        this.id = Nodes.computeId(nodes);
    }

    @Override
    public boolean contains(Node node) {
        return nodes.contains(node);
    }

    @Override
    public Nodes copyR() {
        // Since this is immutable, we can safely return the same reference
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Iterator<Node> iterator() {
        return DataUtils.immutableIterator(nodes);
    }
}
