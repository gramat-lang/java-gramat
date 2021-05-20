package org.gramat.graphs;

import lombok.extern.slf4j.Slf4j;
import org.gramat.data.links.Links;
import org.gramat.data.links.LinksW;
import org.gramat.data.nodes.Nodes;
import org.gramat.errors.ErrorFactory;
import org.gramat.graphs.links.Link;
import org.gramat.graphs.links.LinkEmpty;
import org.gramat.graphs.links.LinkEnter;
import org.gramat.graphs.links.LinkExit;
import org.gramat.graphs.links.LinkSymbol;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class NodeMapper {

    private final Graph graph;
    private final Map<Node, Node> mapping;

    public NodeMapper(Graph graph) {
        this.graph = graph;
        this.mapping = new HashMap<>();
    }

    public Node unmapNode(Node oldNode) {
        var newNode = mapping.get(oldNode);

        if (newNode == null) {
            throw ErrorFactory.internalError("node not mapped");
        }

        return newNode;
    }

    public Nodes unmapNodes(Nodes oldNodes) {
        var newNodes = Nodes.createW();

        for (var oldNode : oldNodes) {
            newNodes.add(unmapNode(oldNode));
        }

        return newNodes;
    }

    public Nodes mapNodes(Nodes oldNodes) {
        var newNodes = Nodes.createW();

        for (var oldNode : oldNodes) {
            newNodes.add(mapNode(oldNode));
        }

        return newNodes;
    }

    public Node mapNode(Node oldNode) {
        return mapping.computeIfAbsent(oldNode, k -> {
            var newNode = graph.createNode();

            log.debug("MAP {} -> {}", oldNode, newNode);

            newNode.wildcard = oldNode.wildcard;

            return newNode;
        });
    }

    public Links searchLinks(Links oldLinks, Links newLinks) {
        var results = Links.createW();

        for (var oldLink : oldLinks) {
            searchLink(oldLink, newLinks, results);
        }

        return results;
    }

    private void searchLink(Link oldLink, Links newLinks, LinksW results) {
        var newSource = unmapNode(oldLink.source);
        var newTarget = unmapNode(oldLink.target);
        var missing = true;

        for (var newLink : newLinks) {
            if (matches(newSource, newTarget, oldLink, newLink)) {
                results.add(newLink);
                missing = false;
            }
        }

        if (missing) {
            throw ErrorFactory.internalError("missing link mapping: " + oldLink);
        }
    }

    private boolean matches(Node newSource, Node newTarget, Link oldLink, Link newLink) {
        if (newLink.source == newSource && newLink.target == newTarget) {
            if (oldLink instanceof LinkSymbol oldSym && newLink instanceof LinkSymbol newSym) {
                return oldSym.symbol == newSym.symbol;
            }
            else if (oldLink instanceof LinkEmpty && newLink instanceof LinkEmpty) {
                return true;
            }
            else {
                throw new UnsupportedOperationException();
            }
        }
        else {
            return false;
        }
    }

}
