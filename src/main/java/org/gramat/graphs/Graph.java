package org.gramat.graphs;

import org.gramat.data.Actions;
import org.gramat.data.Nodes;
import org.gramat.symbols.Symbol;
import org.gramat.tools.IdentifierProvider;
import org.gramat.tools.Validations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Graph {
    public final IdentifierProvider ids;
    public final List<Node> nodes;
    public final List<Link> links;

    public Graph(IdentifierProvider ids) {
        this.ids = ids;
        this.nodes = new ArrayList<>();
        this.links = new ArrayList<>();
    }

    public Node createNode() {
        var node = new Node(ids.next());

        nodes.add(node);

        return node;
    }

    public void createLink(Node source, Node target, Symbol symbol) {
        links.add(new LinkSymbol(source, target, null, null, symbol));
    }

    public void createLink(Node source, Node target) {
        links.add(new LinkEmpty(source, target));
    }

    public void createLink(Nodes sources, Node target, Symbol symbol, Actions beginActions, Actions endActions) {
        createLink(sources, Nodes.of(target), symbol, beginActions, endActions);
    }

    public void createLink(Node source, Nodes targets, Symbol symbol, Actions beginActions, Actions endActions) {
        createLink(Nodes.of(source), targets, symbol, beginActions, endActions);
    }

    public void createLink(Nodes sources, Nodes targets, Symbol symbol, Actions beginActions, Actions endActions) {
        Validations.notEmpty(sources);
        Validations.notEmpty(targets);

        for (var source : sources) {
            for (var target : targets) {
                links.add(new LinkSymbol(source, target, beginActions, endActions, symbol));
            }
        }
    }

    public void createLink(Node source, Node target, Symbol symbol, Actions beginActions, Actions endActions) {
        links.add(new LinkSymbol(source, target, beginActions, endActions, symbol));
    }

    public void createLink(Node source, Nodes targets) {
        createLink(Nodes.of(source), targets);
    }

    public void createLink(Nodes sources, Node target) {
        createLink(sources, Nodes.of(target));
    }

    public void createLink(Nodes sources, Nodes targets) {
        Validations.notEmpty(sources);
        Validations.notEmpty(targets);

        for (var source : sources) {
            for (var target : targets) {
                links.add(new LinkEmpty(source, target));
            }
        }
    }
}
