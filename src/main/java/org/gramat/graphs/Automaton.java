package org.gramat.graphs;

import org.gramat.data.Nodes;

import java.util.List;

public class Automaton {

    public final Node initial;
    public final Nodes accepted;
    public final List<Link> links;

    public Automaton(Node initial, Nodes accepted, List<Link> links) {
        this.initial = initial;
        this.accepted = accepted.copyR();
        this.links = links;
    }
}
