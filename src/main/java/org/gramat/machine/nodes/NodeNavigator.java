package org.gramat.machine.nodes;

import java.util.ArrayDeque;
import java.util.HashSet;

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

    public NodeSet getVisited() {
        return NodeSet.of(control);
    }
}
