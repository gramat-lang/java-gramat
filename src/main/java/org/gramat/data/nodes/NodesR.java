package org.gramat.data.nodes;

import org.gramat.errors.ErrorFactory;
import org.gramat.graphs.Node;
import org.gramat.tools.DataUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class NodesR implements Nodes {

    final Set<Node> nodes;
    final String id;

    NodesR(Collection<Node> nodes) {
        this.nodes = new LinkedHashSet<>(nodes);
        this.id = Nodes.computeId(nodes); // TODO make it lazy
    }

    public NodesR(Nodes first, Node last) {
        if (first instanceof NodesW w) {
            this.nodes = new LinkedHashSet<>(w.nodes);
            this.nodes.add(last);
        }
        else if (first instanceof NodesR r) {
            this.nodes = new LinkedHashSet<>(r.nodes);
            this.nodes.add(last);
        }
        else if (first instanceof Nodes1 n) {
            this.nodes = Set.of(n.node, last);
        }
        else {
            throw ErrorFactory.notImplemented();
        }

        this.id = Nodes.computeId(this.nodes);
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
    public NodesW copyW() {
        return new NodesW(nodes);
    }

    @Override
    public boolean isPresent() {
        return !nodes.isEmpty();
    }

    @Override
    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Iterator<Node> iterator() {
        return DataUtils.immutableIterator(nodes);
    }

    @Override
    public String toString() {
        return nodes.toString();
    }
}
