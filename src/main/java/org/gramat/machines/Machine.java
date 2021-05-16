package org.gramat.machines;

import org.gramat.symbols.Symbol;
import org.gramat.symbols.SymbolChar;
import org.gramat.tools.DataUtils;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Machine {

    public final Node source;
    public final Node target;
    public final List<Link> links;

    public Machine(Node source, Node target, List<Link> links) {
        this.source = Objects.requireNonNull(source);
        this.target = Objects.requireNonNull(target);
        this.links = DataUtils.immutableCopy(links);
    }

    public Set<Symbol> getSymbols() {
        var symbols = new LinkedHashSet<Symbol>();

        for (var link : links) {
            if (link instanceof LinkSymbol linkSym) {
                symbols.add(linkSym.symbol);
            }
        }

        return symbols;
    }
}
