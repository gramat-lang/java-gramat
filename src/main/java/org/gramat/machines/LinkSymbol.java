package org.gramat.machines;

import org.gramat.actions.Action;
import org.gramat.symbols.Symbol;
import org.gramat.tools.DataUtils;

import java.util.Objects;
import java.util.Set;

public class LinkSymbol extends LinkAction {

    public final Symbol symbol;
    public final String token;

    public LinkSymbol(Node source, Node target, Set<Action> beginActions, Set<Action> endActions, Symbol symbol, String token) {
        super(source, target, beginActions, endActions);
        this.symbol = Objects.requireNonNull(symbol);
        this.token = token;
    }


}
