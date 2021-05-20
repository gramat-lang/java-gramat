package org.gramat.graphs;

import org.gramat.data.links.Links;
import org.gramat.data.nodes.Nodes;
import org.gramat.expressions.ActionType;

public class MachineAction {
    public final ActionType type; // TODO action type?
    public final String argument;
    public final Nodes sources;
    public final Nodes targets;
    public final Links links;

    public MachineAction(ActionType type, String argument, Nodes sources, Nodes targets, Links links) {
        this.type = type;
        this.argument = argument;
        this.sources = sources;
        this.targets = targets;
        this.links = links;
    }
}
