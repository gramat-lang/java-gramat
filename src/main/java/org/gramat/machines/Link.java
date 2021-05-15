package org.gramat.machines;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class Link {

    public final Node source;
    public final Node target;

    protected Link(Node source, Node target) {
        this.source = Objects.requireNonNull(source);
        this.target = Objects.requireNonNull(target);
    }

    public static List<Link> findFrom(Node source, List<Link> links) {
        return findFrom(Set.of(source), links);
    }

    public static List<Link> findFrom(Set<Node> sources, List<Link> links) {
        var result = new ArrayList<Link>();

        for (var link : links) {
            if (sources.contains(link.source)) {
                result.add(link);
            }
        }

        return result;
    }

    public static List<Link> findTo(Set<Node> targets, List<Link> links) {
        var result = new ArrayList<Link>();

        for (var link : links) {
            if (targets.contains(link.target)) {
                result.add(link);
            }
        }

        return result;
    }
}
