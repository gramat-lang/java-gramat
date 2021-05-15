package org.gramat.symbols;

import java.util.ArrayList;
import java.util.List;

public class SymbolFactory {

    private static final List<Symbol> symbols = new ArrayList<>();

    public static SymbolChar character(char value) {
        for (var symbol : symbols) {
            if (symbol instanceof SymbolChar) {
                var sc = (SymbolChar) symbol;

                if (sc.value == value) {
                    return sc;
                }
            }
        }

        var symbol = new SymbolChar(value);

        symbols.add(symbol);

        return symbol;
    }

    public static SymbolRange range(char begin, char end) {
        for (var symbol : symbols) {
            if (symbol instanceof SymbolRange) {
                var sr = (SymbolRange) symbol;

                if (sr.begin == begin && sr.end == end) {
                    return sr;
                }
            }
        }

        var symbol = new SymbolRange(begin, end);

        symbols.add(symbol);

        return symbol;
    }

    private SymbolFactory() {}
}
