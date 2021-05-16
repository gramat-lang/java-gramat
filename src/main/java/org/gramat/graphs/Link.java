package org.gramat.graphs;

import org.gramat.symbols.Symbol;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class Link {

    public final Node source;
    public final Node target;

    protected Link(Node source, Node target) {
        this.source = Objects.requireNonNull(source);
        this.target = Objects.requireNonNull(target);
    }

    public static List<Link> findFrom(Node source, List<Link> links) {
        var result = new ArrayList<Link>();

        for (var link : links) {
            if (link.source == source) {
                result.add(link);
            }
        }

        return result;
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

    public static List<Link> findTo(Node target, List<Link> links) {
        var result = new ArrayList<Link>();

        for (var link : links) {
            if (link.target == target) {
                result.add(link);
            }
        }

        return result;
    }

    public static Set<Node> forwardClosure(Node source, List<Link> links) {
        return forwardClosure(Set.of(source), links);
    }

    public static Set<Node> forwardClosure(Set<Node> sources, List<Link> links) {
        var result = new NodeSet();
        var queue = new ArrayDeque<>(sources);

        while (!queue.isEmpty()) {
            var source = queue.remove();
            if (result.add(source)) {
                for (var link : findFrom(source, links)) {
                    if (link instanceof LinkEmpty) {
                        queue.add(link.target);
                    }
                }
            }
        }

        return result;
    }

    public static List<Link> forwardClosure(Set<Node> sources, Symbol symbol, List<Link> links) {
        var result = new ArrayList<Link>();
        var queue = new ArrayDeque<>(sources);
        var control = new HashSet<Node>();

        while (!queue.isEmpty()) {
            var source = queue.remove();
            if (control.add(source)) {
                for (var link : findFrom(source, links)) {
                    if (link instanceof LinkEmpty) {
                        queue.add(link.target);
                    }
                    else if (link instanceof LinkSymbol linkSym && linkSym.symbol == symbol) {
                        result.add(link);
                    }
                }
            }
        }

        return result;
    }

    public static Set<Node> backwardClosure(Node source, List<Link> links) {
        return backwardClosure(Set.of(source), links);
    }

    public static Set<Node> backwardClosure(Set<Node> targets, List<Link> links) {
        var result = new NodeSet();
        var queue = new ArrayDeque<>(targets);

        while (!queue.isEmpty()) {
            var target = queue.remove();
            if (result.add(target)) {
                for (var link : findTo(target, links)) {
                    if (link instanceof LinkEmpty) {
                        queue.add(link.source);
                    }
                }
            }
        }

        return result;
    }

    public static Set<Node> collectTargets(List<? extends Link> links) {
        var result = new NodeSet();

        for (var link : links) {
            result.add(link.target);
        }

        return result;
    }
}
