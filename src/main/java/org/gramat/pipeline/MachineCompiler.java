package org.gramat.pipeline;

import org.gramat.actions.ActionFactory;
import org.gramat.actions.Actions;
import org.gramat.machine.Machine;
import org.gramat.machine.MachineProgram;
import org.gramat.machine.links.LinkList;
import org.gramat.machine.nodes.Node;
import org.gramat.machine.nodes.NodeFactory;
import org.gramat.machine.nodes.NodeSet;
import org.gramat.patterns.Pattern;
import org.gramat.patterns.PatternReference;
import org.gramat.patterns.PatternFactory;

import java.util.HashMap;
import java.util.LinkedHashSet;
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
        return MachineCleaner.run(nodeFactory,
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
                        link.getBeforeActions(), link.getAfterActions());

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

    private Segment compile(String name, Machine machine, Actions rootBeforeActions, Actions rootAfterActions) {
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

            Actions beforeActions;
            Actions afterActions;

            if (fromSource) {
                beforeActions = Actions.join(rootBeforeActions, link.getBeforeActions());
            }
            else {
                beforeActions = link.getBeforeActions();
            }

            if (toTarget) {
                afterActions = Actions.join(rootAfterActions, link.getAfterActions());
            }
            else {
                afterActions = link.getAfterActions();
            }


            if (fromSource && !toTarget) {
                beforeActions = Actions.join(ActionFactory.push(name), beforeActions);
            }

            if (toTarget && !fromSource) {
                afterActions = Actions.join(afterActions, ActionFactory.pop(name));
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
                            beforeActions, afterActions);

                    // Connect segment to the main machine
                    linkList.createLink(link.getSource(), segment.source());
                    linkList.createLink(segment.targets(), link.getTarget());
                }
            }
            else {
                Pattern pattern;

                if (newTargets.contains(linkTarget) && (fromSource ^ toTarget)) {
                    pattern = PatternFactory.token(link.getPattern(), name);
                }
                else {
                    pattern = link.getPattern();
                }

                var newLink = linkList.createLink(linkSource, linkTarget, pattern);
                newLink.addBeforeActions(beforeActions);
                newLink.addAfterActions(afterActions);
            }
        }

        return result;
    }
}
