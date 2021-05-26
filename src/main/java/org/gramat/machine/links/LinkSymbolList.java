package org.gramat.machine.links;

import org.gramat.machine.nodes.Node;
import org.gramat.symbols.Symbol;

import java.util.ArrayList;
import java.util.Iterator;

public class LinkSymbolList implements Iterable<LinkSymbol> {

    final ArrayList<LinkSymbol> data;

    public LinkSymbolList() {
        this.data = new ArrayList<>();
    }

    public LinkSymbol createLink(Node source, Node target, Symbol symbol) {
        var link = new LinkSymbol(source, target, symbol);
        data.add(link);
        return link;
    }

    @Override
    public Iterator<LinkSymbol> iterator() {
        return data.iterator();
    }
}
