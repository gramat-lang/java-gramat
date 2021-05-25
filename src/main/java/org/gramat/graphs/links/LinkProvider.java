package org.gramat.graphs.links;

import org.gramat.data.nodes.Nodes;
import org.gramat.graphs.Node;
import org.gramat.symbols.Symbol;

import java.util.ArrayList;
import java.util.List;

public class LinkProvider {

    private final ArrayList<Link> links;

    public LinkProvider() {
        this.links = new ArrayList<>();
    }

    public LinkSymbol createLink(Node source, Node target, Symbol symbol) {
        var link = new LinkSymbol(source, target, symbol);
        links.add(link);
        return link;
    }

    public void createLink(Nodes sources, Node target) {
        for (var source : sources) {
            links.add(new LinkEmpty(source, target));
        }
    }

    public void createLink(Node source, Nodes targets) {
        for (var target : targets) {
            links.add(new LinkEmpty(source, target));
        }
    }

    public void createLink(Nodes sources, Nodes targets) {
        for (var source : sources) {
            for (var target : targets) {
                links.add(new LinkEmpty(source, target));
            }
        }
    }

    public void createLink(Node source, Node target) {
        links.add(new LinkEmpty(source, target));
    }

    public void addLink(Link link) {
        this.links.add(link);
    }

    public void addLinks(List<? extends Link> links) {
        this.links.addAll(links);
    }

    public List<Link> toList() {
        return links;
    }

    public List<LinkSymbol> toListSymbol() {
        var result = new ArrayList<LinkSymbol>(links.size());

        for (var link : links) {
            if (link instanceof LinkSymbol linkSym) {
                result.add(linkSym);
            }
            else {
                throw new RuntimeException();
            }
        }

        return result;
    }

}
