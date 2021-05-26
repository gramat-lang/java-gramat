package org.gramat.machine.nodes;

import org.gramat.tools.DataUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

class NodeSetSingleton implements NodeSet {

    private final Node node;

    private Collection<Node> data;

    NodeSetSingleton(Node node) {
        this.node = node;
    }

    @Override
    public Collection<Node> toCollection() {
        if (data == null) {
            data = Set.of(node);
        }
        return data;
    }

    @Override
    public NodeSet join(NodeSet nodes) {
        var newData = new LinkedHashSet<Node>();
        newData.add(node);
        newData.addAll(nodes.toCollection());
        return NodeSet.of(newData);
    }

    @Override
    public boolean contains(Node node) {
        return this.node == node;
    }

    @Override
    public String getId() {
        return String.valueOf(node.id);
    }

    @Override
    public Iterator<Node> iterator() {
        return DataUtils.iteratorOf(node);
    }
}
