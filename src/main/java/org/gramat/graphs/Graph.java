package org.gramat.graphs;

import org.gramat.data.links.Links;
import org.gramat.data.links.LinksW;
import org.gramat.data.nodes.Nodes;
import org.gramat.graphs.links.LinkEmpty;
import org.gramat.graphs.links.LinkSymbol;
import org.gramat.symbols.Symbol;
import org.gramat.tools.IdentifierProvider;
import org.gramat.tools.Validations;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    public final IdentifierProvider ids;
    public final List<Node> nodes;
    public final LinksW links;

    public Graph(IdentifierProvider ids) {
        this.ids = ids;
        this.nodes = new ArrayList<>();
        this.links = Links.createW();
    }

    public Node createNode() {
        var node = new Node(ids.next());

        nodes.add(node);

        return node;
    }

    public void createLink(Node source, Node target, Symbol symbol) {
        links.add(new LinkSymbol(source, target, symbol));
    }

    public void createLink(Node source, Node target) {
        links.add(new LinkEmpty(source, target));
    }

    public void createLink(Nodes sources, Nodes targets, Symbol symbol) {
        Validations.notEmpty(sources);
        Validations.notEmpty(targets);

        for (var source : sources) {
            for (var target : targets) {
                links.add(new LinkSymbol(source, target, symbol));
            }
        }
    }

}
