package org.gramat.machine;

import org.gramat.machine.links.LinkPatternList;
import org.gramat.machine.nodes.Node;
import org.gramat.machine.nodes.NodeSet;

public record Machine(Node source, NodeSet targets, LinkPatternList links) {

}
