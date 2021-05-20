package org.gramat.graphs.links;

import org.gramat.graphs.Node;
import org.gramat.symbols.Symbol;

import java.util.Objects;
import java.util.Set;

public class LinkSymbol extends Link {

    public final Symbol symbol;

    public LinkSymbol(String ids, Node source, Node target, Symbol symbol) {
        super(ids, source, target);
        this.symbol = Objects.requireNonNull(symbol);
    }

    @Override
    public String toString() {
        return String.format("Link{%s -> %s : %s}", source, target, symbol);
    }

}
