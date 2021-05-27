package org.gramat.automata.symbols;

import org.gramat.machine.patterns.Pattern;
import org.gramat.machine.patterns.PatternChar;
import org.gramat.machine.patterns.PatternRange;

import java.util.ArrayList;
import java.util.List;

public class SymbolFactory {

    private final List<Symbol> symbols;

    public SymbolFactory() {
        symbols = new ArrayList<>();
    }

    public Symbol symbol(char value) {
        for (var symbol : symbols) {
            if (symbol instanceof SymbolChar sc && sc.getValue() == value) {
                return sc;
            }
        }

        var sc = new SymbolChar(value);
        symbols.add(sc);
        return sc;
    }

    public Symbol symbol(char begin, char end) {
        for (var symbol : symbols) {
            if (symbol instanceof SymbolRange sr && sr.getBegin() == begin && sr.getEnd() == end) {
                return sr;
            }
        }

        var sr = new SymbolRange(begin, end);
        symbols.add(sr);
        return sr;
    }

    public Symbol symbol(Pattern pattern) {
        if (pattern instanceof PatternChar pc) {
            return symbol(pc.value);
        }
        else if (pattern instanceof PatternRange pr) {
            return symbol(pr.begin, pr.end);
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    public Symbol[] toArray() {
        return symbols.toArray(new Symbol[0]);
    }
}
