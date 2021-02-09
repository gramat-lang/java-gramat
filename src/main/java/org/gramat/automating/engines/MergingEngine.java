package org.gramat.automating.engines;

import org.gramat.actions.design.ActionTemplate;
import org.gramat.automating.Automaton;
import org.gramat.automating.DeterministicMachine;
import org.gramat.automating.Direction;
import org.gramat.automating.Machine;
import org.gramat.automating.State;
import org.gramat.automating.transitions.Transition;
import org.gramat.automating.transitions.TransitionAction;
import org.gramat.automating.transitions.TransitionEmpty;
import org.gramat.automating.transitions.TransitionRecursion;
import org.gramat.automating.transitions.TransitionSymbol;
import org.gramat.codes.Code;
import org.gramat.exceptions.GramatException;
import org.gramat.inputs.Location;
import org.gramat.logging.Logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MergingEngine {

    public static DeterministicMachine resolve(Machine nMachine, Logger logger) {
        return new MergingEngine(logger).resolve(nMachine);
    }

    private final Logger logger;
    private final Map<String, Set<State>> idClosures;
    private final Map<String, State> idStates;
    private final Automaton dAuto;

    private MergingEngine(Logger logger) {
        this.logger = logger;
        this.idClosures = new LinkedHashMap<>();
        this.idStates = new LinkedHashMap<>();
        this.dAuto = new Automaton();
    }

    private DeterministicMachine resolve(Machine nMachine) {
        var nAuto = nMachine.am;
        var queue = new LinkedList<Set<State>>();
        var control = new HashSet<String>();
        var closure0 = emptyClosure(nAuto, Set.of(nMachine.begin), null);

        queue.add(closure0);

        var levels = nAuto.listLevels();
        var codes = nAuto.listCodes();

        logger.debug("ResolvingExpression %s codes and %s levels...", codes.size(), levels.size());

        do {
            var closure = queue.remove();
            var closureID = computeID(closure);
            if (control.add(closureID)) {
                var dSource = mapClosure(closure, closureID);

                for (var code : codes) {
                    var transitionSymbols = findTransitionSymbols(nAuto, closure, code);
                    var transitionSources = collectSources(transitionSymbols);
                    var transitionTargets = collectTargets(transitionSymbols);
                    var sourceTransitions = PathEngine.between(closure, transitionSources);
                    var targetTransitions = new ArrayList<Transition>();
                    var targetClosure = emptyClosure(nAuto, transitionTargets, targetTransitions);
                    var targetClosureID = computeID(targetClosure);
                    var dTarget = mapClosure(targetClosure, targetClosureID);

                    var beginActions = listActions(sourceTransitions, Direction.FORWARD);
                    var endActions = listActions(targetTransitions, Direction.BACKWARD);

                    dAuto.addMerged(dSource, dTarget, code, beginActions, endActions);

                    queue.add(targetClosure);
                }
            }
        }
        while (!queue.isEmpty());

        var begin = unmapClosure(closure0);
        var ends = unmapWith(nMachine.end);
        return new DeterministicMachine(dAuto, begin, ends);
    }

    private Set<State> collectSources(Set<? extends Transition> transitions) {
        var sources = new LinkedHashSet<State>();
        for (var t : transitions) {
            sources.add(t.source);
        }
        return sources;
    }

    private Set<State> collectTargets(Set<? extends Transition> transitions) {
        var sources = new LinkedHashSet<State>();
        for (var t : transitions) {
            sources.add(t.target);
        }
        return sources;
    }

    private List<ActionTemplate> listActions(Collection<Transition> transitions, Direction dir) {
        var actions = new ArrayList<ActionTemplate>();

        for (var t : transitions) {
            if (t instanceof TransitionAction) {
                var ta = (TransitionAction) t;

                if (ta.direction == dir) {
                    actions.add(ta.action);
                }
            }
            else if (t instanceof TransitionRecursion) {
                var tr = (TransitionRecursion) t;

                if (tr.direction == dir) {
                    actions.add(tr.action);
                }
            }
            // TODO evaluate other transition types
        }

        return actions;
    }

    private static Set<State> emptyClosure(Automaton am, Set<State> sources, List<Transition> transitions) {
        var states = new LinkedHashSet<State>();
        var control = new HashSet<State>();
        var queue = new ArrayDeque<>(sources);

        while (!queue.isEmpty()) {
            var state = queue.remove();

            if (control.add(state)) {
                states.add(state);

                for (var t : am.findTransitions(state, Direction.FORWARD)) {
                    if (t instanceof TransitionEmpty || t instanceof TransitionAction || t instanceof TransitionRecursion) {
                        queue.add(t.target);

                        if (transitions != null) {
                            transitions.add(t);
                        }
                    }
                    else if (!(t instanceof TransitionSymbol)) {
                        throw new RuntimeException();
                    }
                }
            }
        }

        return states;
    }

    private State mapClosure(Set<State> closure, String closureID) {
        return idStates.computeIfAbsent(closureID, k -> {
            var locations = new LinkedHashSet<Location>();

            for (var s : closure) {
                locations.addAll(s.locations);
            }

            var state = dAuto.createState(locations);

            idClosures.put(closureID, closure);

            return state;
        });
    }

    private Set<TransitionSymbol> findTransitionSymbols(Automaton am, Set<State> source, Code code) {
        var result = new LinkedHashSet<TransitionSymbol>();
        for (var state : source) {
            for (var t : am.findTransitions(state, Direction.FORWARD)) {
                if (t instanceof TransitionSymbol) {
                    var ts = (TransitionSymbol) t;

                    if (code.equals(ts.code)) {
                        result.add(ts);
                    }
                }
                else {
                    // TODO check for unsupported transitions
                }
            }
        }
        return result;
    }

    private State unmapClosure(Set<State> closure) {
        var closureID = computeID(closure);
        var state = idStates.get(closureID);
        if (state == null) {
            throw new GramatException("state not found: " + closureID);
        }
        return state;
    }

    private Set<State> unmapWith(State end) {
        var result = new LinkedHashSet<State>();

        for (var closure : idClosures.values()) {
            if (closure.contains(end)) {
                var state = unmapClosure(closure);

                result.add(state);
            }
        }

        return result;
    }

    private static String computeID(Set<State> states) {
        return states.stream()
                .sorted(Comparator.comparingInt(a -> a.id))
                .map(a -> String.valueOf(a.id))
                .collect(Collectors.joining("_"));
    }
}
