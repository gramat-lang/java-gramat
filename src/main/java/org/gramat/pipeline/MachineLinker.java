package org.gramat.pipeline;

import lombok.extern.slf4j.Slf4j;
import org.gramat.data.actions.ActionsW;
import org.gramat.data.links.Links;
import org.gramat.errors.ErrorFactory;
import org.gramat.graphs.Graph;
import org.gramat.graphs.MachineAction;
import org.gramat.graphs.NodeMapper;
import org.gramat.graphs.links.LinkEmpty;
import org.gramat.graphs.links.LinkSymbol;
import org.gramat.graphs.Machine;
import org.gramat.graphs.MachineProgram;
import org.gramat.graphs.Node;
import org.gramat.graphs.Segment;
import org.gramat.symbols.SymbolReference;
import org.gramat.tools.IdentifierProvider;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MachineLinker {

    public static Machine run(MachineProgram program) {
        return new MachineLinker(program.dependencies).run(program.main);
    }

    private final Graph graph;
    private final NodeMapper mapper;
    private final Map<String, Machine> dependencies;
    private final Map<String, Segment> segments;

    private MachineLinker(Map<String, Machine> dependencies) {
        this.dependencies = dependencies;
        this.segments = new HashMap<>();
        this.graph = new Graph(IdentifierProvider.create(1));
        this.mapper = new NodeMapper(graph);
    }

    private Machine run(Machine main) {
        log.debug("Linking main machine...");

        resolveMachine(main, null);

        log.debug("Linking completed: {} link(s)", graph.links.getCount());

        var source = mapper.unmapNode(main.source);
        var targets = mapper.unmapNodes(main.targets);
        return new Machine(source, targets, graph.links, graph.actions);
    }

    private void resolveMachine(Machine machine, String name) {
        log.debug("Resolving {} machine...", name != null ? name : "main");

        var initialLinks = graph.links.copyW();

        for (var link : machine.links) {
            var newSource = mapper.mapNode(link.source);
            var newTarget = mapper.mapNode(link.target);
            if (link instanceof LinkSymbol linkSym) {
                if (linkSym.symbol instanceof SymbolReference ref) {
                    var dependency = findDependency(ref.name);

                    linkMachine(
                            newSource, newTarget,
                            ref.name, dependency,
                            linkSym.beginActions, linkSym.endActions);
                }
                else {
                    graph.createLink(newSource, newTarget, linkSym.symbol, linkSym.beginActions, linkSym.endActions);
                }
            }
            else if (link instanceof LinkEmpty linkEmp) {
                graph.createLink(newSource, newTarget, linkEmp.beginActions, linkEmp.endActions);
            }
            else {
                throw ErrorFactory.notImplemented();
            }
        }

        // Compute links created by the compiled expression
        var newLinks = graph.links.copyW();
        newLinks.removeAll(initialLinks);

        resolveActions(machine, newLinks, name);
    }

    private void resolveActions(Machine oldMachine, Links newLinks, String name) {
        log.debug("Resolving {} actions...", name);

        var count = 0;

        for (var oldAction : oldMachine.actions) {
            var newAction = new MachineAction(
                    oldAction.type, oldAction.argument,
                    mapper.unmapNodes(oldAction.sources),
                    mapper.unmapNodes(oldAction.targets),
                    mapper.searchLinks(oldAction.links, newLinks)
            );

            graph.actions.add(newAction);
            count++;
        }

        log.debug("Resolving actions completed: {} action(s)", count);
    }

    private void linkMachine(Node newSource, Node newTarget, String name, Machine dependency, ActionsW beginActions, ActionsW endActions) {
        var segment = segments.get(name);  // NOSONAR java:S3824 Cannot use computeIfAbsent without affect logic
        if (segment == null) {
            var source = mapper.mapNode(dependency.source);
            var targets = mapper.mapNodes(dependency.targets);

            segment = new Segment(source, targets);

            segments.put(name, segment);  // This avoids infinite loop in the next line

            resolveMachine(dependency, name);
        }

        graph.createEnter(
                newSource, segment.source, name,
                beginActions, endActions);
        graph.createExit(
                segment.targets, newTarget, name,
                beginActions, endActions);
    }

    private Machine findDependency(String name) {
        var dependency = dependencies.get(name);
        if (dependency == null) {
            throw ErrorFactory.notFound("missing dependency: " + name);
        }
        return dependency;
    }

}
