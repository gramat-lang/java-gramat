package org.gramat.pipeline;

import lombok.extern.slf4j.Slf4j;
import org.gramat.actions.Action;
import org.gramat.errors.ErrorFactory;
import org.gramat.machines.Graph;
import org.gramat.machines.GraphMapper;
import org.gramat.machines.Link;
import org.gramat.machines.Machine;
import org.gramat.machines.MachineContract;
import org.gramat.machines.MachineProgram;
import org.gramat.machines.Node;
import org.gramat.machines.RecursionSymbol;
import org.gramat.tools.DataUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class MachineLinker {

    public static MachineContract run(MachineProgram program) {
        return new MachineLinker(program.dependencies).run(program.main);
    }

    private final Graph graph;
    private final Map<String, Machine> dependencies;
    private final GraphMapper mapper;
    private final Map<String, RecursiveSegment> recursiveSegments;

    private MachineLinker(Map<String, Machine> dependencies) {
        this.dependencies = dependencies;
        this.graph = new Graph();
        this.mapper = new GraphMapper(graph);
        this.recursiveSegments = new HashMap<>();
    }

    private MachineContract run(Machine main) {
        resolveMachine(main);

        var sources = mapper.unmap(main.source);
        var targets = mapper.unmap(main.targets);
        return new MachineContract(sources, targets, graph.links);
    }

    private void resolveMachine(Machine machine) {
        var nonRecursiveLinks = new ArrayList<Link>();

        // First, only copy recursive links
        for (var link : machine.links) {
            if (link.symbol instanceof RecursionSymbol) {
                var recursion = (RecursionSymbol) link.symbol;
                var recursiveSegment = recursiveSegments.get(recursion.name);
                if (recursiveSegment != null) {
                    log.debug("Connecting recursion {}", recursion.name);
                    connectSegment(
                            recursion.name,
                            mapper.map(link.source), mapper.map(link.target),
                            link.beginActions, link.endActions,
                            recursiveSegment);
                }
                else {
                    log.debug("Creating recursion {}", recursion.name);

                    var dependency = findDependency(recursion.name);
                    var newSources = mapper.map(link.source);
                    var newTargets = mapper.map(dependency.targets);

                    recursiveSegments.put(recursion.name, new RecursiveSegment(newSources, newTargets));

                    mapper.map(link.target, newTargets);
                    mapper.map(newSources, link.source);

                    resolveRecursion(recursion.name,
                            newSources, newTargets, dependency,
                            link.beginActions, link.endActions);
                }
            }
            else {
                nonRecursiveLinks.add(link);
            }
        }

        // Then, copy non-recursive
        for (var link : nonRecursiveLinks) {
            var newSources = mapper.map(link.source);
            var newTargets = mapper.map(link.target);

            graph.createLink(newSources, newTargets, link.symbol, null, link.beginActions, link.endActions);
        }
    }

    private void resolveRecursion(String name, Set<Node> rootSources, Set<Node> rootTargets, Machine machine, Set<Action> rootBeginActions, Set<Action> rootEndActions) {
        var nonRecursiveLinks = new ArrayList<Link>();

        // Recursive links first
        for (var link : machine.links) {
            if (link.symbol instanceof RecursionSymbol) {
                var linkInfo = computeLinkInfo(link, machine, rootSources, rootTargets, rootBeginActions, rootEndActions);
                var recursion = (RecursionSymbol) link.symbol;
                var recursiveSegment = recursiveSegments.get(recursion.name);
                if (recursiveSegment != null) {
                    connectSegment(recursion.name,
                            linkInfo.newSources, linkInfo.newTargets,
                            linkInfo.beginActions, linkInfo.endActions,
                            recursiveSegment);
                }
                else {
                    recursiveSegments.put(recursion.name, new RecursiveSegment(linkInfo.newSources, linkInfo.newTargets));

                    resolveRecursion(
                            recursion.name,
                            linkInfo.newSources, linkInfo.newTargets,
                            findDependency(recursion.name),
                            linkInfo.beginActions, linkInfo.endActions);
                }
            }
            else {
                nonRecursiveLinks.add(link);
            }
        }

        for (var link : nonRecursiveLinks) {
            var linkInfo = computeLinkInfo(link, machine, rootSources, rootTargets, rootBeginActions, rootEndActions);
            graph.createLink(
                    linkInfo.newSources, linkInfo.newTargets,
                    link.symbol, null,
                    linkInfo.beginActions, linkInfo.endActions);
        }
    }

    private void connectSegment(String name, Set<Node> newSources, Set<Node> newTargets, Set<Action> beginActions, Set<Action> endActions, RecursiveSegment recursiveSegment) {
        for (var fromLink : Link.findFrom(recursiveSegment.sources, graph.links)) {
            graph.createLink(
                    newSources, fromLink.target,
                    fromLink.symbol, name,
                    DataUtils.join(beginActions, fromLink.beginActions),
                    fromLink.endActions);
        }

        for (var toLink : Link.findTo(recursiveSegment.targets, graph.links)) {
            graph.createLink(
                    toLink.source, newTargets,
                    toLink.symbol, name,
                    toLink.beginActions,
                    DataUtils.join(toLink.endActions, endActions));
        }
    }

    private RecursiveLinkInfo computeLinkInfo(Link link, Machine machine, Set<Node> rootSources, Set<Node> rootTargets, Set<Action> rootBeginActions, Set<Action> rootEndActions) {
        Set<Node> newSources;
        Set<Node> newTargets;
        Set<Action> beginActions;
        Set<Action> endActions;

        var dir = computeDirection(link, machine);
        if (dir == Direction.S_S) {
            newSources = rootSources;
            newTargets = rootSources;
            beginActions = DataUtils.join(rootBeginActions, link.beginActions);
            endActions = link.endActions;
        }
        else if (dir == Direction.S_T) {
            newSources = rootSources;
            newTargets = rootTargets;
            beginActions = DataUtils.join(rootBeginActions, link.beginActions);
            endActions = link.endActions;
        }
        else if (dir == Direction.S_N) {
            newSources = rootSources;
            newTargets = mapper.map(link.target);
            beginActions = DataUtils.join(rootBeginActions, link.beginActions);
            endActions = link.endActions;
        }
        else if (dir == Direction.T_S) {
            newSources = rootTargets;
            newTargets = rootSources;
            beginActions = link.beginActions;
            endActions = link.endActions;
        }
        else if (dir == Direction.T_T) {
            newSources = rootTargets;
            newTargets = rootTargets;
            beginActions = link.beginActions;
            endActions = link.endActions;
        }
        else if (dir == Direction.T_N) {
            newSources = rootTargets;
            newTargets = mapper.map(link.target);
            beginActions = link.beginActions;
            endActions = link.endActions;
        }
        else if (dir == Direction.N_S) {
            newSources = mapper.map(link.source);
            newTargets = rootSources;
            beginActions = link.beginActions;
            endActions = link.endActions;
        }
        else if (dir == Direction.N_T) {
            newSources = mapper.map(link.source);
            newTargets = rootTargets;
            beginActions = link.beginActions;
            endActions = link.endActions;
        }
        else if (dir == Direction.N_N) {
            newSources = mapper.map(link.source);
            newTargets = mapper.map(link.target);
            beginActions = link.beginActions;
            endActions = link.endActions;
        }
        else {
            throw ErrorFactory.internalError("not implemented: " + dir);
        }

        return new RecursiveLinkInfo(newSources, newTargets, beginActions, endActions);
    }

    private Machine findDependency(String name) {
        var dependency = dependencies.get(name);
        if (dependency == null) {
            throw ErrorFactory.notFound("missing dependency: " + name);
        }
        return dependency;
    }

    private Direction computeDirection(Link link, Machine machine) {
        var fromSource = (machine.source == link.source);
        var fromTarget = (machine.targets.contains(link.source));
        var toSource = (machine.source == link.target);
        var toTarget = (machine.targets.contains(link.target));

        if (fromSource) {
            if (toSource) {
                return Direction.S_S;
            } else if (toTarget) {
                return Direction.S_T;
            } else {
                return Direction.S_N;
            }
        }
        else if (fromTarget) {
            if (toSource) {
                return Direction.T_S;
            } else if (toTarget) {
                return Direction.T_T;
            } else {
                return Direction.T_N;
            }
        }
        else {
            if (toSource) {
                return Direction.N_S;
            } else if (toTarget) {
                return Direction.N_T;
            } else {
                return Direction.N_N;
            }
        }
    }

    private static class RecursiveLinkInfo {
        public final Set<Node> newSources;
        public final Set<Node> newTargets;
        public final Set<Action> beginActions;
        public final Set<Action> endActions;
        public RecursiveLinkInfo(Set<Node> newSources, Set<Node> newTargets, Set<Action> beginActions, Set<Action> endActions) {
            this.newSources = newSources;
            this.newTargets = newTargets;
            this.beginActions = beginActions;
            this.endActions = endActions;
        }
    }

    private static class RecursiveSegment {
        public final Set<Node> sources;
        public final Set<Node> targets;
        public RecursiveSegment(Set<Node> sources, Set<Node> targets) {
            this.sources = sources;
            this.targets = targets;
        }
    }

    private enum Direction {
        S_S, S_T, S_N,
        T_S, T_T, T_N,
        N_S, N_T, N_N,
    }

}
