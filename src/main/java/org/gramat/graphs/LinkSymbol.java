package org.gramat.graphs;

import org.gramat.data.Actions;
import org.gramat.symbols.Symbol;

import java.util.Objects;

public class LinkSymbol extends LinkAction {

    public final Symbol symbol;

    public LinkSymbol(Node source, Node target, Actions beginActions, Actions endActions, Symbol symbol) {
        super(source, target, beginActions, endActions);
        this.symbol = Objects.requireNonNull(symbol);
    }

}
