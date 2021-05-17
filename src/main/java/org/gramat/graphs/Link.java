package org.gramat.graphs;

import org.gramat.data.Nodes;
import org.gramat.data.NodesW;
import org.gramat.symbols.Symbol;
import org.gramat.tools.DataUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
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

    public static List<Link> findFrom(Nodes sources, List<Link> links) {
        var result = new ArrayList<Link>();

        for (var link : links) {
            if (sources.contains(link.source)) {
                result.add(link);
            }
        }

        return result;
    }

    public static List<Link> findTo(Nodes targets, List<Link> links) {
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

    public static Nodes forwardClosure(Node source, List<Link> links) {
        return forwardClosure(Nodes.of(source), links);
    }

    public static Nodes forwardClosure(Nodes sources, List<Link> links) {
        var result = Nodes.createW();
        var queue = new ArrayDeque<Node>();

        DataUtils.addAll(queue, sources);

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

    public static List<LinkSymbol> forwardSymbols(Node initial, List<Link> links) {
        var result = new ArrayList<LinkSymbol>();
        var queue = new ArrayDeque<Node>();
        var control = new HashSet<Node>();

        queue.add(initial);

        while (!queue.isEmpty()) {
            var source = queue.remove();
            if (control.add(source)) {
                for (var link : findFrom(source, links)) {
                    if (link instanceof LinkEmpty) {
                        queue.add(link.target);
                    }
                    else if (link instanceof LinkSymbol linkSym) {
                        result.add(linkSym);
                    }
                    else {
                        throw new UnsupportedOperationException();
                    }
                }
            }
        }

        return result;
    }

    public static List<LinkSymbol> backwardSymbols(Node initial, List<Link> links) {
        var result = new ArrayList<LinkSymbol>();
        var queue = new ArrayDeque<Node>();
        var control = new HashSet<Node>();

        queue.add(initial);

        while (!queue.isEmpty()) {
            var target = queue.remove();
            if (control.add(target)) {
                for (var link : findTo(target, links)) {
                    if (link instanceof LinkEmpty) {
                        queue.add(link.source);
                    }
                    else if (link instanceof LinkSymbol linkSym) {
                        result.add(linkSym);
                    }
                    else {
                        throw new UnsupportedOperationException();
                    }
                }
            }
        }

        return result;
    }

    public static List<LinkSymbol> forwardSymbols(Nodes sources, Symbol symbol, List<Link> links) {
        var result = new ArrayList<LinkSymbol>();
        var queue = new ArrayDeque<Node>();
        var control = new HashSet<Node>();

        DataUtils.addAll(queue, sources);

        while (!queue.isEmpty()) {
            var source = queue.remove();
            if (control.add(source)) {
                for (var link : findFrom(source, links)) {
                    if (link instanceof LinkEmpty) {
                        queue.add(link.target);
                    }
                    else if (link instanceof LinkSymbol linkSym) {
                        if (linkSym.symbol == symbol) {
                            result.add(linkSym);
                        }
                    }
                    else {
                        throw new UnsupportedOperationException();
                    }
                }
            }
        }

        return result;
    }

    public static Nodes backwardClosure(Node source, List<Link> links) {
        return backwardClosure(Nodes.of(source), links);
    }

    public static Nodes backwardClosure(Nodes targets, List<Link> links) {
        var result = Nodes.createW();
        var queue = new ArrayDeque<Node>();

        DataUtils.addAll(queue, targets);

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

    public static Nodes collectTargets(List<? extends Link> links) {
        var result = Nodes.createW();

        for (var link : links) {
            result.add(link.target);
        }

        return result;
    }
}
