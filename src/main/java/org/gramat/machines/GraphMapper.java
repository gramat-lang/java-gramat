package org.gramat.machines;

import lombok.extern.slf4j.Slf4j;
import org.gramat.errors.ErrorFactory;
import org.gramat.tools.DataUtils;
import org.gramat.tools.Validations;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class GraphMapper {

    private final Graph graph;
    private final Map<Node, Set<Node>> mapping;

    public GraphMapper(Graph graph) {
        this.graph = graph;
        this.mapping = new HashMap<>();
    }

    public void map(Node oldNode, Node newNode) {
        map(oldNode, Set.of(newNode));
    }

    public void map(Set<Node> oldNodes, Node newNode) {
        Validations.notEmpty(oldNodes);

        for (var oldNode : oldNodes) {
            map(oldNode, newNode);
        }
    }

    public void map(Node oldNode, Set<Node> newNodes) {
        Validations.notEmpty(newNodes);

        if (mapping.containsKey(oldNode)) {
            // throw ErrorFactory.internalError(String.format("Already mapped: %s -> %s", oldNode, mapping.get(oldNode)));
            mapping.put(oldNode, DataUtils.join(newNodes, mapping.get(oldNode)));
        }
        else {
            mapping.put(oldNode, DataUtils.immutableCopy(newNodes));

            log.debug("Mapping {} -> {}", oldNode, newNodes);
        }
    }

    public Set<Node> unmap(Node oldNode) {
        if (mapping.containsKey(oldNode)) {
            return mapping.get(oldNode);
        }
        else {
            throw ErrorFactory.internalError("not found");
        }
    }

    public Set<Node> unmap(Set<Node> oldNodes) {
        var result = new NodeSet();

        for (var oldNode : oldNodes) {
            result.addAll(unmap(oldNode));
        }

        return result;
    }

    public Set<Node> map(Node oldNode) {
        if (mapping.containsKey(oldNode)) {
            return mapping.get(oldNode);
        }
        else {
            var newNode = duplicate(oldNode);
            var newNodes = Set.of(newNode);

            mapping.put(oldNode, newNodes);

            log.debug("Mapping {} -> {}", oldNode, newNodes);

            return newNodes;
        }
    }

    public Set<Node> map(Set<Node> oldNodes) {
        var result = new NodeSet();

        for (var oldNode : oldNodes) {
            result.addAll(map(oldNode));
        }

        return result;
    }

    private Node duplicate(Node node) {
        var newNode = graph.createNode();

        newNode.wildcard = node.wildcard;

        return newNode;
    }

}
