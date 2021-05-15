package org.gramat.symbols;

import org.gramat.tools.PP;

public class SymbolChar implements Symbol {
    public final char value;

    SymbolChar(char value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return PP.ch(value);
    }
}
