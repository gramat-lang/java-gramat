package org.gramat.data.nodes;

import org.gramat.graphs.Node;

import java.util.ArrayList;
import java.util.Comparator;

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
