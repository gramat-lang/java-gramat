package org.gramat.pipeline;

import lombok.extern.slf4j.Slf4j;
import org.gramat.actions.Action;
import org.gramat.actions.ActionFactory;
import org.gramat.data.actions.Actions;
import org.gramat.data.actions.ActionsW;
import org.gramat.data.links.Links;
import org.gramat.data.nodes.Nodes;
import org.gramat.expressions.ActionType;
import org.gramat.graphs.Automaton;
import org.gramat.graphs.ClosureMapper;
import org.gramat.graphs.Graph;
import org.gramat.graphs.links.LinkAction;
import org.gramat.graphs.Machine;
import org.gramat.graphs.links.LinkEnter;
import org.gramat.graphs.links.LinkExit;
import org.gramat.tools.IdentifierProvider;

import java.util.ArrayDeque;
import java.util.HashSet;

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
            var oldSourcesId = oldSources.getId();
            if (control.add(oldSourcesId)) {
                var newSource = mapper.map(oldSources, oldSourcesId);

                for (var symbol : symbols) {
                    var oldLinks = machine.links.findFrom(oldSources, symbol);
                    if (oldLinks.isPresent()) {
                        var oldLinksSources = oldLinks.collectTargets();
                        var oldLinksTargets = oldLinks.collectTargets();
                        var oldTargets = machine.links.forwardClosure(oldLinksTargets);
                        var oldTargetsId = oldTargets.getId();
                        var newTarget = mapper.map(oldTargets, oldTargetsId);
                        var beginActions = Actions.createW();
                        var endActions = Actions.createW();

                        log.debug("NEW LINK {} -> {}: {}", oldSources, oldTargets, symbol);

                        createActions(
                                machine.links.backwardLinksClosure(oldLinksSources),
                                oldLinks,
                                machine.links.forwardLinksClosure(oldLinksTargets),
                                beginActions,
                                endActions);

                        graph.createLink(newSource, newTarget, symbol, beginActions, endActions);

                        queue.add(oldTargets);
                    }
                }
            }
        }

        applyActions(machine);

        log.debug("Compiling machine completed");

        return createAutomaton(closure0, machine);
    }

    private void applyActions(Machine machine) {
        log.debug("Applying actions...");

        for (var action : machine.actions) {
            var newSources = mapper.searchNodes(action.sources);
            var newTarget = mapper.searchNodes(action.targets);
            var newLinks = mapper.searchLinks(action.links, graph.links);

            applyActions(action.type, action.argument, newSources, newTarget, newLinks);
        }
    }

    private void applyActions(ActionType type, String argument, Nodes newSources, Nodes newTarget, Links newLinks) {
        var beginAction = createBeginAction(type);
        var endAction = createEndAction(type, argument);
        var ignoreBeginAction = ActionFactory.ignore(beginAction);
        var cancelEndAction = ActionFactory.cancel(endAction);

        for (var link : newLinks) {
            if (link instanceof LinkAction linkAct) {
                var fromSource = newSources.contains(linkAct.source);
                var fromTarget = newTarget.contains(linkAct.source);
                var toSource = newSources.contains(linkAct.target);
                var toTarget = newTarget.contains(linkAct.target);

                if (fromSource && (linkAct.source != linkAct.target || !fromTarget)) {
                    linkAct.beginActions.append(beginAction);
                }

                if (toTarget) {
                    linkAct.endActions.prepend(endAction);
                }

                if (fromTarget && (linkAct.source == link.target)) {
                    linkAct.beginActions.prepend(cancelEndAction);
                }

                if (toSource && !fromSource) {
                    linkAct.beginActions.append(ignoreBeginAction);
                }
            }
        }
    }

    public Action createBeginAction(ActionType type) {
        return switch (type) {
            case KEY -> ActionFactory.keyBegin();
            case LIST -> ActionFactory.listBegin();
            case MAP -> ActionFactory.mapBegin();
            case PUT -> ActionFactory.putBegin();
            case VALUE -> ActionFactory.valueBegin();
        };
    }

    public Action createEndAction(ActionType type, String argument) {
        return switch (type) {
            case KEY -> ActionFactory.keyEnd(argument);
            case LIST -> ActionFactory.listEnd(argument);
            case MAP -> ActionFactory.mapEnd(argument);
            case PUT -> ActionFactory.putEnd(argument);
            case VALUE -> ActionFactory.valueEnd(argument);
        };
    }

    private void createActions(Links beginLinks, Links links, Links endLinks, ActionsW beginActions, ActionsW endActions) {
        for (var link : beginLinks) {
            if (link instanceof LinkEnter linkEnt) {
                beginActions.prepend(ActionFactory.push(linkEnt.token));
            }
            else if (link instanceof LinkExit linkExt) {
                beginActions.prepend(ActionFactory.pop(linkExt.token));
            }
            else if (link instanceof LinkAction linkAct) {
                beginActions.append(linkAct.beginActions);
                endActions.prepend(linkAct.endActions);
            }
        }

        for (var link : links) {
            if (link instanceof LinkEnter linkEnt) {
                beginActions.prepend(ActionFactory.push(linkEnt.token));
            }
            else if (link instanceof LinkExit linkExt) {
                endActions.prepend(ActionFactory.pop(linkExt.token));
            }
            else if (link instanceof LinkAction linkAct) {
                beginActions.append(linkAct.beginActions);
                endActions.prepend(linkAct.endActions);
            }
        }

        for (var link : endLinks) {
            if (link instanceof LinkEnter linkEnt) {
                endActions.prepend(ActionFactory.push(linkEnt.token));
            }
            else if (link instanceof LinkExit linkExt) {
                endActions.prepend(ActionFactory.pop(linkExt.token));
            }
            else if (link instanceof LinkAction linkAct) {
                beginActions.append(linkAct.beginActions);
                endActions.append(linkAct.endActions);
            }
        }
    }

    private Automaton createAutomaton(Nodes sourceClosure, Machine machine) {
        var initial = mapper.unmap(sourceClosure);
        var targetClosure = machine.links.backwardClosure(machine.target);
        var accepted = mapper.searchNodes(targetClosure);
        return new Automaton(initial, accepted, graph.links);
    }

}
