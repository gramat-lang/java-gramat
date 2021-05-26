package org.gramat.pipeline;

import lombok.extern.slf4j.Slf4j;
import org.gramat.machine.Machine;
import org.gramat.machine.links.Link;
import org.gramat.machine.links.LinkList;
import org.gramat.machine.links.LinkPattern;
import org.gramat.machine.links.LinkPatternList;
import org.gramat.machine.nodes.Node;
import org.gramat.machine.nodes.NodeNavigator;
import org.gramat.machine.nodes.NodeFactory;
import org.gramat.machine.nodes.NodeSet;
import org.gramat.patterns.Pattern;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Slf4j
public class MachineCleaner {

    public static Machine run(NodeFactory nodeFactory, NodeSet sources, NodeSet targets, LinkList links) {
        return new MachineCleaner(nodeFactory).run(sources, targets, links);
    }

    private final NodeFactory nodeFactory;
    private final Map<String, NodeSet> idClosures;
    private final Map<String, Node> idNewNodes;

    private MachineCleaner(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
        this.idClosures = new HashMap<>();
        this.idNewNodes = new HashMap<>();
    }

    private Machine run(NodeSet sources, NodeSet targets, LinkList links) {
        log.debug("Cleaning machine...");

        var patterns = links.getPatterns();
        var control = new HashSet<String>();
        var queue = new ArrayDeque<NodeSet>();
        var cleanLinks = new LinkPatternList();

        var closure0 = forwardClosure(sources, links);
        queue.add(closure0);

        while (!queue.isEmpty()) {
            var oldSources = queue.remove();
            if (control.add(oldSources.getId())) {
                var newSource = map(oldSources);

                for (var pattern : patterns) {
                    var oldLinkPatterns = findLinkPatternsFrom(oldSources, pattern, links);
                    if (!oldLinkPatterns.isEmpty()) {
                        var oldTargets = forwardClosure(collectTargets(oldLinkPatterns), links);
                        log.debug("LINK {} -> {} : {}", oldSources, oldTargets, pattern);

                        var newTarget = map(oldTargets);
                        var newLink = cleanLinks.createLink(newSource, newTarget, pattern);

                        applyActions(newLink, oldLinkPatterns);

                        queue.add(oldTargets);
                    }
                }
            }
        }

        log.debug("Cleaning machine completed");

        var cleanSource = unmap(closure0);
        var cleanTargets = searchNodes(targets);
        return new Machine(cleanSource, cleanTargets, cleanLinks);
    }

    private void applyActions(LinkPattern newLink, List<LinkPattern> oldLinks) {
        // TODO check for collisions
        for (var oldLink : oldLinks) {
            newLink.addBeforeActions(oldLink.getBeforeActions());
            newLink.addAfterActions(oldLink.getAfterActions());
        }
    }

    private NodeSet forwardClosure(NodeSet sources, LinkList links) {
        var nav = new NodeNavigator();

        nav.push(sources);

        while (nav.hasNodes()) {
            var source = nav.pop();
            for (var link : links) {
                if (link.getSource() == source && link.isEmpty()) {
                    nav.push(link.getTarget());
                }
            }
        }

        return nav.getVisited();
    }

    private List<LinkPattern> findLinkPatternsFrom(NodeSet closure, Pattern pattern, LinkList links) {
        var result = new ArrayList<LinkPattern>();

        for (var link : links) {
            if (closure.contains(link.getSource()) && link instanceof LinkPattern linkSym && link.getPattern() == pattern) {
                result.add(linkSym);
            }
        }

        return result;
    }

    private NodeSet collectTargets(List<? extends Link> links) {
        var result = new LinkedHashSet<Node>();

        for (var link : links) {
            result.add(link.getTarget());
        }

        return NodeSet.of(result);
    }


    public Node map(NodeSet nodes) {
        return idNewNodes.computeIfAbsent(nodes.getId(), k -> {
            var newNode = nodeFactory.createNode();
            idClosures.put(nodes.getId(), nodes);
            return newNode;
        });
    }

    public Node unmap(NodeSet oldNodes) {
        var id = oldNodes.getId();
        var newNode = idNewNodes.get(id);
        if (newNode == null) {
            throw new RuntimeException("not mapped");
        }
        return newNode;
    }

    public NodeSet searchNodes(NodeSet oldNodes) {
        var newNodes = new LinkedHashSet<Node>();

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

        return NodeSet.of(newNodes);
    }

}
