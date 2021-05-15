package org.gramat.symbols;

import org.gramat.tools.PP;

public class SymbolRange implements Symbol {
    public final char begin;
    public final char end;

    SymbolRange(char begin, char end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    public String toString() {
        return String.format("%s-%s", PP.ch(begin), PP.ch(end));
    }
}
