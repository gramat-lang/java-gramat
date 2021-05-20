package org.gramat.data.links;

import org.gramat.data.nodes.NodeNavigator;
import org.gramat.data.nodes.Nodes;
import org.gramat.graphs.Node;
import org.gramat.graphs.links.Link;
import org.gramat.graphs.links.LinkEmpty;
import org.gramat.graphs.links.LinkSymbol;
import org.gramat.symbols.Symbol;
import org.gramat.tools.DataUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public interface Links extends Iterable<Link> {

    Links copyR();

    LinksW copyW();

    int getCount();

    boolean isEmpty();

    boolean isPresent();

    static LinksW createW() {
        return new LinksW();
    }

    default Links findFrom(Node source) {
        var result = Links.createW();

        for (var link : this) {
            if (link.source == source) {
                result.add(link);
            }
        }

        return result;
    }

    default Links findFrom(Nodes sources) {
        var result = Links.createW();

        for (var link : this) {
            if (sources.contains(link.source)) {
                result.add(link);
            }
        }

        return result;
    }

    default Links findTo(Nodes targets) {
        var result = Links.createW();

        for (var link : this) {
            if (targets.contains(link.target)) {
                result.add(link);
            }
        }

        return result;
    }

    default Links findTo(Node target) {
        var result = Links.createW();

        for (var link : this) {
            if (link.target == target) {
                result.add(link);
            }
        }

        return result;
    }

    default Nodes forwardClosure(Node source) {
        return forwardClosure(Nodes.of(source));
    }

    default Nodes forwardClosure(Nodes sources) {
        var result = Nodes.createW();
        var queue = new ArrayDeque<Node>();

        DataUtils.addAll(queue, sources);

        while (!queue.isEmpty()) {
            var source = queue.remove();
            if (result.add(source)) {
                for (var link : findFrom(source)) {
                    if (link instanceof LinkEmpty) {
                        queue.add(link.target);
                    }
                }
            }
        }

        return result;
    }

    default List<LinkSymbol> forwardSymbols(Node initial) {
        var result = new ArrayList<LinkSymbol>();
        var queue = new ArrayDeque<Node>();
        var control = new HashSet<Node>();

        queue.add(initial);

        while (!queue.isEmpty()) {
            var source = queue.remove();
            if (control.add(source)) {
                for (var link : findFrom(source)) {
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

    default List<LinkSymbol> backwardSymbols(Node initial) {
        return backwardSymbols(Nodes.of(initial));
    }

    default List<LinkSymbol> backwardSymbols(Nodes initial) {
        var result = new ArrayList<LinkSymbol>();
        var queue = new ArrayDeque<Node>();
        var control = new HashSet<Node>();

        DataUtils.addAll(queue, initial);

        while (!queue.isEmpty()) {
            var target = queue.remove();
            if (control.add(target)) {
                for (var link : findTo(target)) {
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

    default Links findFrom(Nodes sources, Symbol symbol) {
        var result = Links.createW();

        for (var link : this) {
            if (sources.contains(link.source) && link instanceof LinkSymbol linkSym && linkSym.symbol == symbol) {
                result.add(link);
            }
        }

        return result;
    }

    default Links forwardSymbols(Nodes sources, Symbol symbol) {
        var result = Links.createW();
        var queue = new ArrayDeque<Node>();
        var control = new HashSet<Node>();

        DataUtils.addAll(queue, sources);

        while (!queue.isEmpty()) {
            var source = queue.remove();
            if (control.add(source)) {
                for (var link : findFrom(source)) {
                    if (link instanceof LinkEmpty) {
                        queue.add(link.target);
                    }
                    else if (link instanceof LinkSymbol linkSym) {
                        if (linkSym.symbol == symbol) {
                            result.add(linkSym);

                            queue.add(linkSym.target);
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

    default Nodes backwardClosure(Node source) {
        return backwardClosure(Nodes.of(source));
    }

    default Nodes backwardClosure(Nodes targets) {
        var result = Nodes.createW();
        var queue = new ArrayDeque<Node>();

        DataUtils.addAll(queue, targets);

        while (!queue.isEmpty()) {
            var target = queue.remove();
            if (result.add(target)) {
                for (var link : findTo(target)) {
                    if (link instanceof LinkEmpty) {
                        queue.add(link.source);
                    }
                }
            }
        }

        return result;
    }

    default Nodes collectSources() {
        var result = Nodes.createW();

        for (var link : this) {
            result.add(link.source);
        }

        return result;
    }

    default Nodes collectTargets() {
        var result = Nodes.createW();

        for (var link : this) {
            result.add(link.target);
        }

        return result;
    }

    default Links backwardLinksClosure(Nodes nodes) {
        var result = Links.createW();
        var nav = new NodeNavigator();

        nav.push(nodes);

        while (nav.hasNodes()) {
            var target = nav.pop();

            for (var link : findTo(target)) {
                if (link instanceof LinkEmpty) {
                    result.add(link);

                    nav.push(link.source);
                }
            }
        }

        return result;
    }

    default Links forwardLinksClosure(Nodes nodes) {
        var result = Links.createW();
        var nav = new NodeNavigator();

        nav.push(nodes);

        while (nav.hasNodes()) {
            var source = nav.pop();

            for (var link : findFrom(source)) {
                if (link instanceof LinkEmpty) {
                    result.add(link);

                    nav.push(link.target);
                }
            }
        }

        return result;
    }
}
