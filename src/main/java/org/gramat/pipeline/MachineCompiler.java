package org.gramat.pipeline;

import lombok.extern.slf4j.Slf4j;
import org.gramat.actions.Action;
import org.gramat.actions.ActionFactory;
import org.gramat.data.actions.Actions;
import org.gramat.data.actions.ActionsW;
import org.gramat.data.links.Links;
import org.gramat.data.nodes.Nodes;
import org.gramat.errors.ErrorFactory;
import org.gramat.expressions.WrappingType;
import org.gramat.graphs.Automaton;
import org.gramat.graphs.Graph;
import org.gramat.graphs.links.Link;
import org.gramat.graphs.links.LinkAction;
import org.gramat.graphs.Machine;
import org.gramat.graphs.Node;
import org.gramat.graphs.links.LinkEmpty;
import org.gramat.graphs.links.LinkEnter;
import org.gramat.graphs.links.LinkExit;
import org.gramat.graphs.links.LinkSymbol;
import org.gramat.tools.IdentifierProvider;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Slf4j
public class MachineCompiler {

    public static Automaton compile(Machine machine) {
        return new MachineCompiler().run(machine);
    }

    private final Graph graph;
    private final Map<String, Nodes> idClosures;
    private final Map<String, Node> idNewNodes;

    private MachineCompiler() {
        graph = new Graph(IdentifierProvider.create(1));
        idClosures = new HashMap<>();
        idNewNodes = new HashMap<>();
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
                var newSource = map(oldSources, oldSourcesId);

                for (var symbol : symbols) {
                    var oldLinks = machine.links.findFrom(oldSources, symbol);
                    if (oldLinks.isPresent()) {
                        var oldLinksSources = oldLinks.collectTargets();
                        var oldLinksTargets = oldLinks.collectTargets();
                        var oldTargets = machine.links.forwardClosure(oldLinksTargets);
                        var oldTargetsId = oldTargets.getId();
                        var newTarget = map(oldTargets, oldTargetsId);
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
            var newSources = unmap(action.sources);
            var newTarget = unmap(action.targets);
            var newLinks = unmapLinks(action.links, graph.links);

            applyActions(action.type, action.argument, newSources, newTarget, newLinks);
        }
    }

    private void applyActions(WrappingType type, String argument, Nodes newSources, Nodes newTarget, Links newLinks) {
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

    private Links unmapLinks(Links oldLinks, Links newLinks) {
        var results = Links.createW();

        for (var oldLink : oldLinks) {
            var newSources = unmap(Nodes.of(oldLink.source));
            var newTargets = unmap(Nodes.of(oldLink.target));
            var missing = true;

            for (var newLink : newLinks) {
                if (newSources.contains(newLink.source) && newTargets.contains(newLink.target)) {
                    if (oldLink instanceof LinkSymbol oldSym && newLink instanceof LinkSymbol newSym) {
                        if (oldSym.symbol == newSym.symbol) {
                            results.add(newLink);
                            missing = false;
                        }
                    }
                    else if (oldLink instanceof LinkEmpty) {
                        results.add(newLink);
                        missing = false;
                    }
                }
            }

            if (missing) {
                throw ErrorFactory.internalError("missing link mapping: " + oldLink);
            }
        }

        return results;
    }



    public Action createBeginAction(WrappingType type) {
        switch (type) {
            case KEY: return ActionFactory.keyBegin();
            case LIST: return ActionFactory.listBegin();
            case MAP: return ActionFactory.mapBegin();
            case PUT: return ActionFactory.putBegin();
            case VALUE: return ActionFactory.valueBegin();
            default: throw ErrorFactory.internalError("not implemented type: " + type);
        }
    }

    public Action createEndAction(WrappingType type, String argument) {
        switch (type) {
            case KEY:
                if (argument != null) {
                    throw ErrorFactory.internalError("key does not accept arguments");
                }
                return ActionFactory.keyEnd();
            case LIST: return ActionFactory.listEnd(argument);
            case MAP: return ActionFactory.mapEnd(argument);
            case PUT: return ActionFactory.putEnd(argument);
            case VALUE: return ActionFactory.valueEnd(argument);
            default: throw ErrorFactory.internalError("not implemented type: " + type);
        }
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
        var initial = idNewNodes.get(sourceClosure.getId());
        var accepted = Nodes.createW();

        var targetClosure = machine.links.backwardClosure(machine.targets);

        for (var entry : idClosures.entrySet()) {
            for (var target : targetClosure) {
                if (entry.getValue().contains(target)) {
                    var newAccepted = idNewNodes.get(entry.getKey());

                    accepted.add(newAccepted);
                }
            }
        }

        return new Automaton(initial, accepted, graph.links);
    }

    private Node map(Nodes nodes, String id) {
        return idNewNodes.computeIfAbsent(id, k -> {
            var newNode = graph.createNode();
            idClosures.put(id, nodes);
            return newNode;
        });
    }

    private Nodes unmap(Nodes oldNodes) {
        var newNodes = Nodes.createW();

        for (var entry : idClosures.entrySet()) {
            for (var oldNode : oldNodes) {
                if (entry.getValue().contains(oldNode)) {
                    var newNode = idNewNodes.get(entry.getKey());

                    newNodes.add(newNode);
                }
            }
        }

        return newNodes;
    }

}
