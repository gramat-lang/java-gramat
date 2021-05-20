package org.gramat.pipeline;

import lombok.extern.slf4j.Slf4j;
import org.gramat.data.links.Links;
import org.gramat.data.nodes.NodeNavigator;
import org.gramat.data.nodes.Nodes;
import org.gramat.graphs.Automaton;
import org.gramat.graphs.ClosureMapper;
import org.gramat.graphs.Graph;
import org.gramat.graphs.Machine;
import org.gramat.graphs.links.Link;
import org.gramat.graphs.links.LinkEmpty;
import org.gramat.graphs.links.LinkSymbol;
import org.gramat.symbols.Symbol;
import org.gramat.tools.IdentifierProvider;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
public class MachineCompiler {

    public static Automaton compile(Machine machine) {
        return new MachineCompiler().run(machine);
    }

    private final Graph graph;
    private final ClosureMapper mapper;

    private MachineCompiler() {
        graph = new Graph(IdentifierProvider.create(1));
        mapper = new ClosureMapper(graph);
    }

    private Automaton run(Machine machine) {
        log.debug("Compiling machine...");

        var symbols = machine.getSymbols();
        var control = new HashSet<String>();
        var queue = new ArrayDeque<Nodes>();

        var closure0 = machine.links.forwardClosure(machine.source);
        queue.add(closure0);

        while (!queue.isEmpty()) {
            var oldSources = queue.remove();
            if (control.add(oldSources.getId())) {
                var newSource = mapper.map(oldSources);

                for (var symbol : symbols) {
                    var info = computeTargetClosure(oldSources, symbol, machine.links);
                    if (info.closure.isPresent()) {
                        log.debug("LINK {} -> {} : {}", oldSources, info.closure, symbol);

                        var newTarget = mapper.map(info.closure);
                        var newLink = graph.createLink(newSource, newTarget, symbol);

                        info.applyActions(newLink);

                        queue.add(info.closure);
                    }
                }
            }
        }

        log.debug("Compiling machine completed");

        var initial = mapper.unmap(closure0);
        var targetClosure = machine.links.backwardClosure(machine.target);
        var accepted = mapper.searchNodes(targetClosure);
        return new Automaton(initial, accepted, graph.links);
    }

    private ClosureInfo computeTargetClosure(Nodes sources, Symbol symbol, Links links) {
        var nav = new NodeNavigator();
        var beforeLinks = Links.createW();
        var mainLinks = Links.createW();
        var afterLinks = Links.createW();
        var closure = Nodes.createW();

        nav.push(sources);

        while (nav.hasNodes()) {
            var source = nav.pop();

            for (var link : links) {
                if (link.source == source) {
                    if (link instanceof LinkEmpty) {
                        nav.push(link.target);

                        beforeLinks.add(link);
                    }
                    else if (link instanceof LinkSymbol linkSym) {
                        if (linkSym.symbol == symbol) {
                            mainLinks.add(link);

                            closure.add(link.target);
                        }
                    }
                    else {
                        throw new RuntimeException();
                    }
                }
            }
        }

        nav.reset();
        nav.push(closure);

        while (nav.hasNodes()) {
            var source = nav.pop();

            for (var link : links) {
                if (link.source == source) {
                    if (link instanceof LinkEmpty) {
                        nav.push(link.target);

                        afterLinks.add(link);

                        closure.add(link.target);
                    }
                    else if (!(link instanceof LinkSymbol)) {
                        throw new RuntimeException();
                    }
                }
            }
        }

        return new ClosureInfo(beforeLinks, mainLinks, afterLinks, closure);
    }

    private record ClosureInfo(Links beforeLinks, Links mainLinks, Links afterLinks, Nodes closure) {
        public String getIds() {
            var beforeIds = new LinkedHashSet<String>();
            for (var link : beforeLinks) {
                beforeIds.add(link.ids);
            }

            var mainIds = new LinkedHashSet<String>();
            for (var link : mainLinks) {
                mainIds.add(link.ids);
            }

            var aftterIds = new LinkedHashSet<String>();
            for (var link : afterLinks) {
                aftterIds.add(link.ids);
            }

            return String.format("B(%s) M(%s) A(%s)", String.join("-", beforeIds), String.join("-", mainIds), String.join("-", aftterIds));
        }

        public void applyActions(Link link) {
            for (var beforeLink : beforeLinks) {
                link.beforeActions.append(beforeLink.beforeActions);
                link.afterActions.append(beforeLink.afterActions);
            }

            for (var mainLink : mainLinks) {
                link.beforeActions.append(mainLink.beforeActions);
                link.afterActions.append(mainLink.afterActions);
            }

            for (var afterLink : afterLinks) {
                link.beforeActions.append(afterLink.beforeActions);
                link.afterActions.append(afterLink.afterActions);
            }
        }
    }

}
