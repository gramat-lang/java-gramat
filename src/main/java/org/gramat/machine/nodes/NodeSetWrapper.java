package org.gramat.machine.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

class NodeSetWrapper implements NodeSet {

    private final Set<Node> data;

    private String id;

    NodeSetWrapper(Set<Node> data) {
        if (data.isEmpty()) {
            throw new RuntimeException();
        }
        this.data = data;
    }

    @Override
    public boolean contains(Node node) {
        return data.contains(node);
    }

    @Override
    public String getId() {
        if (id == null) {
            id = computeId(data);
        }
        return id;
    }

    @Override
    public Collection<Node> toCollection() {
        return data;
    }

    @Override
    public NodeSet join(NodeSet nodes) {
        var newData = new LinkedHashSet<>(data);
        newData.addAll(nodes.toCollection());
        return new NodeSetWrapper(newData);
    }

    @Override
    public Iterator<Node> iterator() {
        return data.iterator();
    }

    private static String computeId(Iterable<Node> nodes) {
        var ids = new ArrayList<String>();

        for (var node : nodes) {
            ids.add(String.valueOf(node.id));
        }

        ids.sort(Comparator.naturalOrder());

        return String.join("_", ids);
    }
}
