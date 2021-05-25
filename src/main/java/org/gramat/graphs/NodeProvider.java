package org.gramat.graphs;

import org.gramat.tools.IdentifierProvider;

public class NodeProvider {

    private final IdentifierProvider ids;

    public NodeProvider() {
        ids = IdentifierProvider.create(1);
    }

    public Node createNode() {
        return new Node(ids.next());
    }

}
