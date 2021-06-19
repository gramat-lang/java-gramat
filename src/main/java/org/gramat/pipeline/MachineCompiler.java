package org.gramat.pipeline;

import org.gramat.machine.operations.Operation;
import org.gramat.machine.Machine;
import org.gramat.machine.MachineProgram;
import org.gramat.machine.links.LinkList;
import org.gramat.machine.nodes.Node;
import org.gramat.machine.nodes.NodeFactory;
import org.gramat.machine.nodes.NodeSet;
import org.gramat.machine.operations.OperationList;
import org.gramat.machine.patterns.Pattern;
import org.gramat.machine.patterns.PatternFactory;
import org.gramat.machine.patterns.PatternReference;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class MachineCompiler {

    public record Segment(Node source, NodeSet targets) {
        // nothing special here
    }

    public static Machine run(NodeFactory nodeFactory, MachineProgram program) {
        return new MachineCompiler(program.dependencies, nodeFactory).run(program.main);
    }

    private final Map<String, Machine> dependencies;
    private final LinkList linkList;
    private final NodeFactory nodeFactory;
    private final Map<String, Segment> segments;

    private MachineCompiler(Map<String, Machine> dependencies, NodeFactory nodeFactory) {
        this.dependencies = dependencies;
        this.nodeFactory = nodeFactory;
        this.linkList = new LinkList();
        this.segments = new HashMap<>();
    }

    private Machine run(Machine main) {
        var segment = compile(main);
        return PowersetConstruction.run(nodeFactory,
                NodeSet.of(segment.source()),
                segment.targets(),
                linkList);
    }

    private Segment compile(Machine machine) {
        for (var link : machine.links()) {
            if (link.getPattern() instanceof PatternReference ref) {
                var dependency = dependencies.get(ref.name);
                if (dependency == null) {
                    throw new RuntimeException();
                }
                var segment = compile(ref.name, dependency,
                        link.getBeginOperations(), link.getEndOperations());

                // Connect segment to the main machine
                linkList.createLink(link.getSource(), segment.source());
                linkList.createLink(segment.targets(), link.getTarget());
            }
            else {
                linkList.addLink(link);
            }
        }

        return new Segment(machine.source(), machine.targets());
    }

    private Segment compile(String name, Machine machine, OperationList rootBeginOperations, OperationList rootEndOperations) {
        var nodeMap = new HashMap<Node, Node>();
        var newSource = nodeFactory.createNode();
        var newTargets = new LinkedHashSet<Node>();

        nodeMap.put(machine.source(), newSource);

        for (var target : machine.targets()) {
            var newTarget = nodeFactory.createNode();

            nodeMap.put(target, newTarget);

            newTargets.add(newTarget);
        }

        var result = new Segment(newSource, NodeSet.of(newTargets));

        segments.put(name, result);

        for (var link : machine.links()) {
            var linkSource = nodeMap.getOrDefault(link.getSource(), link.getSource());
            var linkTarget = nodeMap.getOrDefault(link.getTarget(), link.getTarget());
            var fromSource = (newSource == linkSource);
            var toTarget = (newTargets.contains(linkTarget));

            OperationList beginOperations;
            OperationList endOperations;

            if (fromSource) {
                beginOperations = OperationList.join(rootBeginOperations, link.getBeginOperations());
            }
            else {
                beginOperations = link.getBeginOperations();
            }

            if (toTarget) {
                endOperations = OperationList.join(link.getEndOperations(), rootEndOperations);
            }
            else {
                endOperations = link.getEndOperations();
            }

            if (link.getPattern() instanceof PatternReference ref) {
                var segment = segments.get(ref.name);
                if (segment != null) {
                    linkList.createLink(link.getSource(), segment.source());
                    linkList.createLink(segment.targets(), link.getTarget());
                }
                else {
                    var dependency = dependencies.get(ref.name);
                    if (dependency == null) {
                        throw new RuntimeException();
                    }

                    segment = compile(
                            ref.name, dependency,
                            beginOperations, endOperations);

                    // Connect segment to the main machine
                    linkList.createLink(link.getSource(), segment.source());
                    linkList.createLink(segment.targets(), link.getTarget());
                }
            }
            else {
                Pattern pattern;

                // Use symbol + token only if the link goes to the target
                //   AND does not come from source (otherwise it would not be reachable)
                if (newTargets.contains(linkTarget) && newSource != linkSource) {
                    pattern = PatternFactory.token(link.getPattern(), name);
                }
                else {
                    pattern = link.getPattern();
                }

                var newLink = linkList.createLink(linkSource, linkTarget, pattern);
                newLink.getBeginOperations().prepend(beginOperations);
                newLink.getEndOperations().append(endOperations);
            }
        }

        return result;
    }
}
