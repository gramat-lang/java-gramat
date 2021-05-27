package org.gramat.machine.links;

import org.gramat.machine.nodes.Node;
import org.gramat.machine.nodes.NodeSet;
import org.gramat.machine.patterns.Pattern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class LinkList implements Iterable<Link> {

    private final ArrayList<Link> data;

    public LinkList() {
        data = new ArrayList<>();
    }

    public LinkPattern createLink(Node source, Node target, Pattern pattern) {
        var link = new LinkPattern(source, target, pattern);
        data.add(link);
        return link;
    }

    public void createLink(NodeSet sources, Node target) {
        for (var source : sources) {
            data.add(new LinkEmpty(source, target));
        }
    }

    public void createLink(Node source, NodeSet targets) {
        for (var target : targets) {
            data.add(new LinkEmpty(source, target));
        }
    }

    public void createLink(NodeSet sources, NodeSet targets) {
        for (var source : sources) {
            for (var target : targets) {
                data.add(new LinkEmpty(source, target));
            }
        }
    }

    public void createLink(Node source, Node target) {
        data.add(new LinkEmpty(source, target));
    }

    public void addLink(Link link) {
        data.add(link);
    }

    public void addLinks(LinkPatternList links) {
        data.addAll(links.data);
    }

    @Override
    public Iterator<Link> iterator() {
        return data.iterator();
    }

    public Set<Pattern> getPatterns() {
        var patterns = new LinkedHashSet<Pattern>();

        for (var link : data) {
            if (link instanceof LinkPattern linkSym) {
                patterns.add(linkSym.getPattern());
            }
        }

        return patterns;
    }
}
