package org.gramat.graphs;

import org.gramat.data.nodes.Nodes;
import org.gramat.graphs.links.LinkSymbol;

import java.util.List;

public record CleanMachine(Node source, Nodes targets, List<LinkSymbol> links) {

}
