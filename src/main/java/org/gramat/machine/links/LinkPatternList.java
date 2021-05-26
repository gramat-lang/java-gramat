package org.gramat.machine.links;

import org.gramat.machine.nodes.Node;
import org.gramat.patterns.Pattern;

import java.util.ArrayList;
import java.util.Iterator;

public class LinkPatternList implements Iterable<LinkPattern> {

    final ArrayList<LinkPattern> data;

    public LinkPatternList() {
        this.data = new ArrayList<>();
    }

    public LinkPattern createLink(Node source, Node target, Pattern pattern) {
        var link = new LinkPattern(source, target, pattern);
        data.add(link);
        return link;
    }

    @Override
    public Iterator<LinkPattern> iterator() {
        return data.iterator();
    }
}
