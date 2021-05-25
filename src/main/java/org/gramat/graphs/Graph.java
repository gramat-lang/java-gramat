package org.gramat.graphs;

import org.gramat.data.links.Links;
import org.gramat.data.links.LinksW;
import org.gramat.graphs.links.Link;
import org.gramat.graphs.links.LinkEmpty;
import org.gramat.graphs.links.LinkSymbol;
import org.gramat.symbols.Symbol;
import org.gramat.tools.IdentifierProvider;

import java.util.ArrayList;
import java.util.List;

public class Graph {

    private final IdentifierProvider linkIds;

    public final IdentifierProvider nodeIds;
    public final List<Node> nodes;
    public final LinksW links;

    public Graph(IdentifierProvider nodeIds) {
        this.nodeIds = nodeIds;
        this.linkIds = IdentifierProvider.create(1);
        this.nodes = new ArrayList<>();
        this.links = Links.createW();
    }

    public Node createNode() {
        var node = new Node(nodeIds.next());

        nodes.add(node);

        return node;
    }

    public Link createLink(Node source, Node target, Symbol symbol) {
        var link = new LinkSymbol(source, target, symbol);

        links.add(link);

        return link;
    }

    public void createLink(Node source, Node target) {
        links.add(new LinkEmpty(source, target));
    }

}
