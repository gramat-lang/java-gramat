package org.gramat.pipeline;

import lombok.extern.slf4j.Slf4j;
import org.gramat.data.nodes.NodeNavigator;
import org.gramat.data.nodes.Nodes;
import org.gramat.graphs.CleanMachine;
import org.gramat.graphs.ClosureMapper;
import org.gramat.graphs.DirtyMachine;
import org.gramat.graphs.links.Link;
import org.gramat.graphs.Node;
import org.gramat.graphs.NodeProvider;
import org.gramat.graphs.links.LinkProvider;
import org.gramat.graphs.links.LinkSymbol;
import org.gramat.symbols.Symbol;
import org.gramat.tools.DataUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
public class MachineCompiler {

    public static CleanMachine compile(NodeProvider nodeProvider, DirtyMachine machine) {
        return new MachineCompiler(nodeProvider).run(machine);
    }

    private final ClosureMapper mapper;

    private MachineCompiler(NodeProvider nodeProvider) {
        mapper = new ClosureMapper(nodeProvider);
    }

    private CleanMachine run(DirtyMachine machine) {
        log.debug("Compiling machine...");

        var symbols = machine.symbols();
        var control = new HashSet<String>();
        var queue = new ArrayDeque<Nodes>();
        var cleanLinks = new LinkProvider();

        var closure0 = forwardClosure(machine.sources(), machine.links());
        queue.add(closure0);

        while (!queue.isEmpty()) {
            var oldSources = queue.remove();
            if (control.add(oldSources.getId())) {
                var newSource = mapper.map(oldSources);

                for (var symbol : symbols) {
                    var info = computeTargetClosure(oldSources, symbol, machine.links());
                    if (info.closure.isPresent()) {
                        log.debug("LINK {} -> {} : {}", oldSources, info.closure, symbol);

                        var newTarget = mapper.map(info.closure);
                        var newLink = cleanLinks.createLink(newSource, newTarget, symbol);

                        info.applyActions(newLink);

                        queue.add(info.closure);
                    }
                }
            }
        }

        log.debug("Compiling machine completed");

        var cleanSource = mapper.unmap(closure0);
        var targetClosure = backwardClosure(machine.targets(), machine.links());
        var cleanTargets = mapper.searchNodes(targetClosure);
        return new CleanMachine(cleanSource, cleanTargets, cleanLinks.toListSymbol());
    }

    private Nodes forwardClosure(Nodes sources, List<Link> links) {
        var result = Nodes.createW();
        var queue = new ArrayDeque<Node>();  // TODO use navigator

        DataUtils.addAll(queue, sources);

        while (!queue.isEmpty()) {
            var source = queue.remove();
            if (result.add(source)) {
                for (var link : links) {
                    if (sources.contains(link.getSource()) && link.isEmpty()) {
                        queue.add(link.getTarget());
                    }
                }
            }
        }

        return result;
    }

    private Nodes backwardClosure(Nodes targets, List<Link> links) {
        var result = Nodes.createW();
        var queue = new ArrayDeque<Node>();  // TODO use navigator

        DataUtils.addAll(queue, targets);

        while (!queue.isEmpty()) {
            var target = queue.remove();
            if (result.add(target)) {
                for (var link : links) {
                    if (targets.contains(link.getTarget()) && link.isEmpty()) {
                        queue.add(link.getSource());
                    }
                }
            }
        }

        return result;
    }

    private ClosureInfo computeTargetClosure(Nodes sources, Symbol symbol, List<Link> links) {
        var nav = new NodeNavigator();
        var beforeLinks = new ArrayList<Link>();
        var mainLinks = new ArrayList<Link>();
        var afterLinks = new ArrayList<Link>();
        var closure = Nodes.createW();

        nav.push(sources);

        while (nav.hasNodes()) {
            var source = nav.pop();

            for (var link : links) {
                if (link.getSource() == source) {
                    if (link.isEmpty()) {
                        nav.push(link.getTarget());

                        beforeLinks.add(link);
                    }
                    else if (link.getSymbol() == symbol) {
                        mainLinks.add(link);

                        closure.add(link.getTarget());
                    }
                }
            }
        }

        nav.reset();
        nav.push(closure);

        while (nav.hasNodes()) {
            var source = nav.pop();

            for (var link : links) {
                if (link.getSource() == source && link.isEmpty()) {
                    nav.push(link.getTarget());

                    afterLinks.add(link);

                    closure.add(link.getTarget());
                }
            }
        }

        return new ClosureInfo(beforeLinks, mainLinks, afterLinks, closure);
    }

    private record ClosureInfo(List<Link> beforeLinks, List<Link> mainLinks, List<Link> afterLinks, Nodes closure) {
        public void applyActions(Link link) {
            for (var beforeLink : beforeLinks) {
                link.addBeforeActions(beforeLink.getBeforeActions());
                link.addAfterActions(beforeLink.getAfterActions());
            }

            for (var mainLink : mainLinks) {
                link.addBeforeActions(mainLink.getBeforeActions());
                link.addAfterActions(mainLink.getAfterActions());
            }

            for (var afterLink : afterLinks) {
                link.addBeforeActions(afterLink.getBeforeActions());
                link.addAfterActions(afterLink.getAfterActions());
            }
        }
    }

}
