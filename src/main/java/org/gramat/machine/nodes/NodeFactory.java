package org.gramat.machine.nodes;

import org.gramat.tools.IdentifierProvider;

public class NodeFactory {

    private final IdentifierProvider ids;

    public NodeFactory() {
        ids = IdentifierProvider.create(1);
    }

    public Node createNode() {
        return new Node(ids.next());
    }

}
