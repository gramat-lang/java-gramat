package org.gramat.automata.symbols;

import org.gramat.tools.PP;

public class SymbolChar implements Symbol {

    private final char value;

    public SymbolChar(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    @Override
    public boolean matches(char c) {
        return value == c;
    }

    @Override
    public String toString() {
        return PP.ch(value);
    }
}
