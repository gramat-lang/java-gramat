package org.gramat.data.nodes;

import org.gramat.graphs.Node;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class NodeNavigator {

    private final ArrayDeque<Node> queue;
    private final HashSet<Node> control;

    public NodeNavigator() {
        queue = new ArrayDeque<>();
        control = new HashSet<>();
    }

    public void reset() {
        queue.clear();
        control.clear();
    }

    public void push(Node node) {
        if (control.add(node)) {
            queue.add(node);
        }
    }

    public void push(Iterable<Node> nodes) {
        for (var node : nodes) {
            push(node);
        }
    }

    public Node pop() {
        return queue.remove();
    }

    public boolean hasNodes() {
        return !queue.isEmpty();
    }

    public Nodes getVisited() {
        return Nodes.of(control);
    }
}
