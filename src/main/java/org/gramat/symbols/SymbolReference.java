package org.gramat.symbols;

import java.util.Objects;

public class SymbolReference implements Symbol {

    public final String name;

    SymbolReference(String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
