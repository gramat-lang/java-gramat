package org.gramat.data.nodes;

import org.gramat.errors.ErrorFactory;
import org.gramat.graphs.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class NodesW implements Nodes {

    final LinkedHashSet<Node> nodes;

    private String cacheId;

    NodesW() {
        nodes = new LinkedHashSet<>();
    }

    NodesW(Collection<Node> nodes) {
        this.nodes = new LinkedHashSet<>(nodes);
    }

    @Override
    public boolean isEmpty() {
        return nodes.isEmpty();
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

    @Override
    public NodesW copyW() {
        return null;
    }

    public boolean add(Node node) {
        cacheId = null;
        return nodes.add(node);
    }

    public void addAll(Nodes nodes) {
        if (nodes instanceof NodesW w) {
            this.nodes.addAll(w.nodes);
        }
        else if (nodes instanceof NodesR r) {
            this.nodes.addAll(r.nodes);
        }
        else if (nodes instanceof Nodes1 n) {
            this.nodes.add(n.node);
        }
        else {
            throw ErrorFactory.notImplemented();
        }
    }

    @Override
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }

    @Override
    public String toString() {
        return nodes.toString();
    }
}
