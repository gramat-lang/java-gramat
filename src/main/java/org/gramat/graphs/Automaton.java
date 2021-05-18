package org.gramat.graphs;

import org.gramat.data.links.Links;
import org.gramat.data.nodes.Nodes;
import org.gramat.graphs.links.Link;

import java.util.List;

public class Automaton {

    public final Node initial;
    public final Nodes accepted;
    public final Links links;

    public Automaton(Node initial, Nodes accepted, Links links) {
        this.initial = initial;
        this.accepted = accepted.copyR();
        this.links = links;
    }
}
