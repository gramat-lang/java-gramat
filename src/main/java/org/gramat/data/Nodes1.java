package org.gramat.data;

import org.gramat.graphs.Node;
import org.gramat.tools.DataUtils;

import java.util.Iterator;
import java.util.List;

public class Nodes1 implements Nodes {

    private final Node node;

    public Nodes1(Node node) {
        this.node = node;
    }

    @Override
    public String getId() {
        return String.valueOf(node.id);
    }

    @Override
    public boolean contains(Node node) {
        return this.node == node;
    }

    @Override
    public Nodes copyR() {
        return new NodesR(List.of(node));
    }

    @Override
    public Iterator<Node> iterator() {
        return DataUtils.iteratorOf(node);
    }
}
