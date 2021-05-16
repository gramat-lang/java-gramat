package org.gramat.graphs;

import java.util.List;
import java.util.Set;

public class Automaton {

    public final Node initial;
    public final Set<Node> accepted;
    public final List<Link> links;

    public Automaton(Node initial, Set<Node> accepted, List<Link> links) {
        this.initial = initial;
        this.accepted = accepted;
        this.links = links;
    }
}
