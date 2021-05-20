package org.gramat.graphs;

import org.gramat.data.links.Links;
import org.gramat.data.links.LinksW;
import org.gramat.data.nodes.Nodes;
import org.gramat.errors.ErrorFactory;
import org.gramat.graphs.links.Link;
import org.gramat.graphs.links.LinkEmpty;
import org.gramat.graphs.links.LinkSymbol;

import java.util.HashMap;
import java.util.Map;

public class ClosureMapper {

    private final Graph graph;
    private final Map<String, Nodes> idClosures;
    private final Map<String, Node> idNewNodes;

    public ClosureMapper(Graph graph) {
        this.graph = graph;
        this.idClosures = new HashMap<>();
        this.idNewNodes = new HashMap<>();
    }

    public Node map(Nodes nodes, String id) {
        return idNewNodes.computeIfAbsent(id, k -> {
            var newNode = graph.createNode();
            idClosures.put(id, nodes);
            return newNode;
        });
    }

    public Node unmap(Nodes oldNodes) {
        var id = oldNodes.getId();
        var newNode = idNewNodes.get(id);
        if (newNode == null) {
            throw new RuntimeException("not mapped");
        }
        return newNode;
    }

    public Nodes searchNode(Node oldNode) {
        return searchNodes(Nodes.of(oldNode));
    }

    public Nodes searchNodes(Nodes oldNodes) {
        var newNodes = Nodes.createW();

        for (var entry : idClosures.entrySet()) {
            for (var oldNode : oldNodes) {
                if (entry.getValue().contains(oldNode)) {
                    var newNode = idNewNodes.get(entry.getKey());

                    newNodes.add(newNode);
                }
            }
        }

        if (newNodes.isEmpty()) {
            throw new RuntimeException();
        }

        return newNodes;
    }

    public Links searchLinks(Links oldLinks, Links newLinks) {
        var results = Links.createW();

        for (var oldLink : oldLinks) {
            searchLink(oldLinks, oldLink, newLinks, results);
        }

        return results;
    }

    private void searchLink(Links oldLinks, Link oldLink, Links newLinks, LinksW results) {
        var newSources = searchNodes(oldLinks.forwardClosure(oldLink.source));
        var newTargets = searchNodes(oldLinks.forwardClosure(oldLink.target));
        var missing = true;

        for (var newLink : newLinks) {
            if (matches(newSources, newTargets, oldLink, newLink)) {
                results.add(newLink);
                missing = false;
            }
        }

        if (missing) {
            throw ErrorFactory.internalError("missing link mapping: " + oldLink);
        }
    }

    private boolean matches(Nodes newSources, Nodes newTargets, Link oldLink, Link newLink) {
        if (newSources.contains(newLink.source) && newTargets.contains(newLink.target)) {
            if (oldLink instanceof LinkSymbol oldSym && newLink instanceof LinkSymbol newSym) {
                return oldSym.symbol == newSym.symbol;
            }
            else if (oldLink instanceof LinkEmpty) {
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
