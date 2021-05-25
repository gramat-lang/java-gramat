package org.gramat.data.nodes;

import org.gramat.graphs.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;

public interface Nodes extends Iterable<Node> {

    static Nodes of(Node node) {
        return new Nodes1(node);
    }

    static NodesW createW() {
        return new NodesW();
    }

    static Nodes join(Nodes first, Node last) {
        return new NodesR(first, last);
    }

    static Nodes join(Node first, Nodes last) {
        // TODO optimize
        var nodes = new LinkedHashSet<Node>();
        nodes.add(first);
        for (var node : last) {
            nodes.add(node);
        }
        return new NodesR(nodes);
    }

    static Nodes join(Nodes first, Nodes last) {
        // TODO optimize
        var nodes = new LinkedHashSet<Node>();
        for (var node : first) {
            nodes.add(node);
        }
        for (var node : last) {
            nodes.add(node);
        }
        return new NodesR(nodes);
    }

    boolean isEmpty();

    static String computeId(Iterable<Node> nodes) {
        var ids = new ArrayList<String>();

        for (var node : nodes) {
            ids.add(String.valueOf(node.id));
        }

        ids.sort(Comparator.naturalOrder());

        return String.join("_", ids);
    }

    String getId();

    boolean contains(Node node);

    Nodes copyR();

    NodesW copyW();

    boolean isPresent();
}
