package org.gramat.graphs;

import org.gramat.data.links.Links;
import org.gramat.symbols.Symbol;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Machine {

    public final Node source;
    public final Node target;
    public final Links links;

    public Machine(Node source, Node target, Links links) {
        this.source = Objects.requireNonNull(source);
        this.target = Objects.requireNonNull(target);
        this.links = links.copyR();
    }

    public Set<Symbol> getSymbols() {
        var symbols = new LinkedHashSet<Symbol>();

        for (var link : links) {
            if (link.hasSymbol()) {
                symbols.add(link.getSymbol());
            }
        }

        return symbols;
    }
}
