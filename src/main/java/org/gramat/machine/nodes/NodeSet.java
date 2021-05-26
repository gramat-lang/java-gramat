package org.gramat.machine.nodes;

import java.util.Collection;
import java.util.Set;

public interface NodeSet extends Iterable<Node> {

    static NodeSet of(Node node) {
        return new NodeSetSingleton(node);
    }

    static NodeSet of(Set<Node> data) {
        return new NodeSetWrapper(data);
    }

    Collection<Node> toCollection();

    NodeSet join(NodeSet nodes);

    boolean contains(Node node);

    String getId();
}
