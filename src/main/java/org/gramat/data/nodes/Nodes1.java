package org.gramat.data.nodes;

import org.gramat.graphs.Node;
import org.gramat.tools.DataUtils;

import java.util.Iterator;
import java.util.List;

public class Nodes1 implements Nodes {

    final Node node;

    public Nodes1(Node node) {
        this.node = node;
    }

    @Override
    public boolean isEmpty() {
        return false;
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
    public NodesW copyW() {
        return new NodesW(List.of(node));
    }

    @Override
    public Iterator<Node> iterator() {
        return DataUtils.iteratorOf(node);
    }

    @Override
    public String toString() {
        return String.format("[%s]", node);
    }
}
