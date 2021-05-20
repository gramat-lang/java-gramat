package org.gramat.graphs;

import org.gramat.data.actions.Actions;
import org.gramat.data.links.Links;
import org.gramat.data.links.LinksW;
import org.gramat.data.nodes.Nodes;
import org.gramat.graphs.links.Link;
import org.gramat.graphs.links.LinkEmpty;
import org.gramat.graphs.links.LinkEnter;
import org.gramat.graphs.links.LinkExit;
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
    public final List<MachineAction> actions = new ArrayList<>();

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

    public void createEnter(Node source, Node target, String token, Actions beginActions, Actions endActions) {
        links.add(new LinkEnter(source, target, beginActions, endActions, token));
    }

    public void createExit(Node source, Node target, String token, Actions beginActions, Actions endActions) {
        links.add(new LinkExit(source, target, beginActions, endActions, token));
    }

    public void createLink(Node source, Nodes targets, Symbol symbol) {
        for (var target : targets) {
            links.add(new LinkSymbol(source, target, null, null, symbol));
        }
    }

    public void createLink(Node source, Node target, Symbol symbol) {
        links.add(new LinkSymbol(source, target, null, null, symbol));
    }

    public void createLink(Node source, Node target) {
        links.add(new LinkEmpty(source, target, null, null));
    }

    public void createLink(Node source, Node target, Actions beginActions, Actions endActions) {
        links.add(new LinkEmpty(source, target, beginActions, endActions));
    }

    public void createLink(Nodes sources, Nodes targets, Actions beginActions, Actions endActions) {
        for (var source : sources) {
            for (var target : targets) {
                links.add(new LinkEmpty(source, target, beginActions, endActions));
            }
        }
    }

    public void createLink(Nodes sources, Node target, Actions beginActions, Actions endActions) {
        for (var source : sources) {
            links.add(new LinkEmpty(source, target, beginActions, endActions));
        }
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

}
