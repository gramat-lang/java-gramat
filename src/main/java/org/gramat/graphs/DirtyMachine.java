package org.gramat.graphs;

import org.gramat.data.nodes.Nodes;
import org.gramat.graphs.links.Link;
import org.gramat.symbols.Symbol;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public record DirtyMachine(Nodes sources, Nodes targets, List<Link> links) {


}
