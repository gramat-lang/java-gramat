package org.gramat.symbols;

import java.util.Objects;

public class SymbolToken implements Symbol {

    public final Symbol symbol;
    public final String token;

    SymbolToken(Symbol symbol, String token) {
        // TODO validate symbol types to avoid deep nesting
        this.symbol = Objects.requireNonNull(symbol);
        this.token = Objects.requireNonNull(token);
    }

    @Override
    public String toString() {
        return symbol + " / " + token;
    }
}
