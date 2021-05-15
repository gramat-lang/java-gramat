package org.gramat.machines;

import org.gramat.actions.Action;
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

    public Graph() {
        this(IdentifierProvider.create(1));
    }

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

    public void createLink(Node source, Node target, Symbol symbol, String token) {
        links.add(new Link(source, target, symbol, token, null, null));
    }

    public void createLink(Set<Node> sources, Node target, Symbol symbol, String token, Set<Action> beginActions, Set<Action> endActions) {
        createLink(sources, Set.of(target), symbol, token, beginActions, endActions);
    }

    public void createLink(Node source, Set<Node> targets, Symbol symbol, String token, Set<Action> beginActions, Set<Action> endActions) {
        createLink(Set.of(source), targets, symbol, token, beginActions, endActions);
    }

    public void createLink(Set<Node> sources, Set<Node> targets, Symbol symbol, String token, Set<Action> beginActions, Set<Action> endActions) {
        Validations.notEmpty(sources);
        Validations.notEmpty(targets);

        for (var source : sources) {
            for (var target : targets) {
                links.add(new Link(source, target, symbol, token, beginActions, endActions));
            }
        }
    }
}
