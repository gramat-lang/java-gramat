package org.gramat.automata.symbols;

public class SymbolRange implements Symbol {

    private final char begin;
    private final char end;

    public SymbolRange(char begin, char end) {
        this.begin = begin;
        this.end = end;
    }

    public char getBegin() {
        return begin;
    }

    public char getEnd() {
        return end;
    }

    @Override
    public boolean matches(char c) {
        return c >= begin && c <= end;
    }
}
