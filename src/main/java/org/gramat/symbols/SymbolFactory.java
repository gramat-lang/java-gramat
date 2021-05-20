package org.gramat.symbols;

import java.util.ArrayList;
import java.util.List;

public class SymbolFactory {

    private static final List<Symbol> symbols = new ArrayList<>();

    public static SymbolChar character(char value) {
        for (var symbol : symbols) {
            if (symbol instanceof SymbolChar sc && sc.value == value) {
                return sc;
            }
        }

        var symbol = new SymbolChar(value);

        symbols.add(symbol);

        return symbol;
    }

    public static SymbolRange range(char begin, char end) {
        for (var symbol : symbols) {
            if (symbol instanceof SymbolRange sr) {

                if (sr.begin == begin && sr.end == end) {
                    return sr;
                }
            }
        }

        var symbol = new SymbolRange(begin, end);

        symbols.add(symbol);

        return symbol;
    }

    public static SymbolToken token(Symbol symbol, String token) {
        for (var s : symbols) {
            if (s instanceof SymbolToken st && st.symbol == symbol && st.token.equals(token)) {
                return st;
            }
        }

        var st = new SymbolToken(symbol, token);

        symbols.add(st);

        return st;
    }

    private SymbolFactory() {}
}
