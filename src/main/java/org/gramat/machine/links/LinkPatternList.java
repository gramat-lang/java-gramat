package org.gramat.machine.links;

import org.gramat.machine.nodes.Node;
import org.gramat.machine.patterns.Pattern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public List<LinkPattern> findAllFrom(Node node) {
        var result = new ArrayList<LinkPattern>();

        for (var link : data) {
            if (link.getSource() == node) {
                result.add(link);
            }
        }

        return result;
    }
}
