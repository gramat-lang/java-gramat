package org.gramat.graphs;

import lombok.extern.slf4j.Slf4j;
import org.gramat.errors.ErrorFactory;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GraphMapper {

    private final Graph graph;
    private final Map<Node, Node> mapping;

    public GraphMapper(Graph graph) {
        this.graph = graph;
        this.mapping = new HashMap<>();
    }
    private Node unmap(Node oldNode) {
        var newNode = mapping.get(oldNode);

        if (newNode == null) {
            throw ErrorFactory.internalError("node not mapped");
        }

        return newNode;
    }

    private Node map(Node oldNode) {
        return mapping.computeIfAbsent(oldNode, k -> {
            var newNode = graph.createNode();

            log.debug("MAP {} -> {}", oldNode, newNode);

            newNode.wildcard = oldNode.wildcard;

            return newNode;
        });
    }

}
