package org.gramat.machines;

import org.gramat.actions.Action;
import org.gramat.symbols.Symbol;
import org.gramat.tools.DataUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Link {

    public final Node source;
    public final Node target;

    public final Symbol symbol;
    public final String token;

    public final Set<Action> beginActions;
    public final Set<Action> endActions;

    public Link(Node source, Node target, Symbol symbol, String token, Set<Action> beginActions, Set<Action> endActions) {
        this.source = Objects.requireNonNull(source);
        this.target = Objects.requireNonNull(target);
        this.symbol = Objects.requireNonNull(symbol);
        this.token = token;
        this.beginActions = DataUtils.mutableCopy(beginActions);
        this.endActions = DataUtils.mutableCopy(endActions);
    }

    public static List<Link> findFrom(Node source, List<Link> links) {
        return findFrom(Set.of(source), links);
    }

    public static List<Link> findFrom(Set<Node> sources, List<Link> links) {
        var result = new ArrayList<Link>();

        for (var link : links) {
            if (sources.contains(link.source)) {
                result.add(link);
            }
        }

        return result;
    }

    public static List<Link> findTo(Set<Node> targets, List<Link> links) {
        var result = new ArrayList<Link>();

        for (var link : links) {
            if (targets.contains(link.target)) {
                result.add(link);
            }
        }

        return result;
    }
}
