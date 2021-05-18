package org.gramat.graphs;

import org.gramat.data.links.Links;
import org.gramat.data.nodes.Nodes;
import org.gramat.graphs.links.Link;
import org.gramat.graphs.links.LinkSymbol;
import org.gramat.symbols.Symbol;
import org.gramat.tools.DataUtils;
import org.gramat.tools.Validations;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Machine {

    public final Node source;
    public final Nodes targets;
    public final Links links;

    public Machine(Node source, Nodes targets, Links links) {
        Validations.notEmpty(targets);

        this.source = Objects.requireNonNull(source);
        this.targets = Objects.requireNonNull(targets);
        this.links = links.copyR();
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
