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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class MachineCleaner {

    public static CleanMachine run(NodeProvider nodeProvider, Nodes sources, Nodes targets, List<Link> links) {
        return new MachineCleaner(nodeProvider).run(sources, targets, links);
    }

    private final ClosureMapper mapper;

    private MachineCleaner(NodeProvider nodeProvider) {
        mapper = new ClosureMapper(nodeProvider);
    }

    private CleanMachine run(Nodes sources, Nodes targets, List<Link> links) {
        log.debug("Cleaning machine...");

        var symbols = symbols(links);
        var control = new HashSet<String>();
        var queue = new ArrayDeque<Nodes>();
        var cleanLinks = new LinkProvider();

        var closure0 = forwardClosure(sources, links);
        queue.add(closure0);

        while (!queue.isEmpty()) {
            var oldSources = queue.remove();
            if (control.add(oldSources.getId())) {
                var newSource = mapper.map(oldSources);

                for (var symbol : symbols) {
                    var oldLinkSymbols = findLinkSymbolsFrom(oldSources, symbol, links);
                    if (!oldLinkSymbols.isEmpty()) {
                        var oldTargets = forwardClosure(collectTargets(oldLinkSymbols), links);
                        log.debug("LINK {} -> {} : {}", oldSources, oldTargets, symbol);

                        var newTarget = mapper.map(oldTargets);
                        var newLink = cleanLinks.createLink(newSource, newTarget, symbol);

                        applyActions(newLink, oldLinkSymbols);

                        queue.add(oldTargets);
                    }
                }
            }
        }

        log.debug("Cleaning machine completed");

        var cleanSource = mapper.unmap(closure0);
        var targetClosure = backwardClosure(targets, links);
        var cleanTargets = mapper.searchNodes(targetClosure);
        return new CleanMachine(cleanSource, cleanTargets, cleanLinks.toListSymbol());
    }

    private void applyActions(LinkSymbol newLink, List<LinkSymbol> oldLinks) {
        // TODO check for collisions
        for (var oldLink : oldLinks) {
            newLink.addBeforeActions(oldLink.getBeforeActions());
            newLink.addAfterActions(oldLink.getAfterActions());
        }
    }

    private Nodes forwardClosure(Nodes sources, List<Link> links) {
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

    private List<LinkSymbol> findLinkSymbolsFrom(Nodes closure, Symbol symbol, List<Link> links) {
        var result = new ArrayList<LinkSymbol>();

        for (var link : links) {
            if (closure.contains(link.getSource()) && link instanceof LinkSymbol linkSym && link.getSymbol() == symbol) {
                result.add(linkSym);
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

    private static Set<Symbol> symbols(List<Link> links) {
        var symbols = new LinkedHashSet<Symbol>();

        for (var link : links) {
            if (!link.isEmpty()) {
                symbols.add(link.getSymbol());
            }
        }

        return symbols;
    }

    private Nodes collectTargets(List<? extends Link> links) {
        var result = Nodes.createW();

        for (var link : links) {
            result.add(link.getTarget());
        }

        return result;
    }

}
