package org.gramat.graphs.links;

import org.gramat.data.actions.Actions;
import org.gramat.graphs.Node;
import org.gramat.symbols.Symbol;

import java.util.Objects;

public class LinkSymbol extends LinkAction {

    public final Symbol symbol;

    public LinkSymbol(Node source, Node target, Actions beginActions, Actions endActions, Symbol symbol) {
        super(source, target, beginActions, endActions);
        this.symbol = Objects.requireNonNull(symbol);
    }

    @Override
    public String toString() {
        return String.format("Link{%s -> %s : %s}", source, target, symbol);
    }

}
