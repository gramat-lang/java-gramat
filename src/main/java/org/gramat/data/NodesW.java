package org.gramat.data;

import org.gramat.graphs.Node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class NodesW implements Nodes {

    private final LinkedHashSet<Node> nodes;

    private String cacheId;

    NodesW() {
        nodes = new LinkedHashSet<>();
    }

    @Override
    public String getId() {
        if (cacheId == null) {
            var ids = new ArrayList<String>();

            for (var node : nodes) {
                ids.add(String.valueOf(node.id));
            }

            ids.sort(Comparator.naturalOrder());

            cacheId = String.join("_", ids);
        }
        return cacheId;
    }

    @Override
    public boolean contains(Node node) {
        return nodes.contains(node);
    }

    @Override
    public Nodes copyR() {
        return new NodesR(nodes);
    }

    public boolean add(Node node) {
        cacheId = null;
        return nodes.add(node);
    }

    @Override
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }
}
