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

    public void createLink(Set<Node> sources, Node target, String name, String token, Set<Action> beginActions, Set<Action> endActions) {
        createLink(sources, Set.of(target), name, token, beginActions, endActions);
    }

    public void createLink(Node source, Set<Node> targets, String name, String token, Set<Action> beginActions, Set<Action> endActions) {
        createLink(Set.of(source), targets, name, token, beginActions, endActions);
    }

    public void createLink(Node source, Node target, String name, String token, Set<Action> beginActions, Set<Action> endActions) {
        links.add(new LinkReference(source, target, beginActions, endActions, name, token));
    }

    public void createLink(Set<Node> sources, Set<Node> targets, String name, String token, Set<Action> beginActions, Set<Action> endActions) {
        Validations.notEmpty(sources);
        Validations.notEmpty(targets);

        for (var source : sources) {
            for (var target : targets) {
                links.add(new LinkReference(source, target, beginActions, endActions, name, token));
            }
        }
    }

    public void createLink(Node source, Node target, Symbol symbol, String token) {
        links.add(new LinkSymbol(source, target, null, null, symbol, token));
    }

    public void createLink(Node source, Node target, String name, String token) {
        links.add(new LinkReference(source, target, null, null, name, token));
    }

    public void createLink(Node source, Node target) {
        links.add(new LinkEmpty(source, target));
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
                links.add(new LinkSymbol(source, target, beginActions, endActions, symbol, token));
            }
        }
    }

    public void createLink(Node source, Node target, Symbol symbol, String token, Set<Action> beginActions, Set<Action> endActions) {
        links.add(new LinkSymbol(source, target, beginActions, endActions, symbol, token));
    }

    public void createLink(Node source, Set<Node> targets) {
        createLink(Set.of(source), targets);
    }

    public void createLink(Set<Node> sources, Node target) {
        createLink(sources, Set.of(target));
    }

    public void createLink(Set<Node> sources, Set<Node> targets) {
        Validations.notEmpty(sources);
        Validations.notEmpty(targets);

        for (var source : sources) {
            for (var target : targets) {
                links.add(new LinkEmpty(source, target));
            }
        }
    }
}
