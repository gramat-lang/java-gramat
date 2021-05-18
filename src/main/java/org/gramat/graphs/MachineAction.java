package org.gramat.graphs;

import org.gramat.data.links.Links;
import org.gramat.data.nodes.Nodes;
import org.gramat.expressions.WrappingType;

public class MachineAction {
    public final WrappingType type; // TODO action type?
    public final String argument;
    public final Nodes sources;
    public final Nodes targets;
    public final Links links;

    public MachineAction(WrappingType type, String argument, Nodes sources, Nodes targets, Links links) {
        this.type = type;
        this.argument = argument;
        this.sources = sources;
        this.targets = targets;
        this.links = links;
    }
}
