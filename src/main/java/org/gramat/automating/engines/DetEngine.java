package org.gramat.automating.engines;

import org.gramat.actions.design.ActionRole;
import org.gramat.actions.design.ActionTemplate;
import org.gramat.automating.Automaton;
import org.gramat.automating.DeterministicMachine;
import org.gramat.automating.State;
import org.gramat.automating.transitions.Transition;
import org.gramat.automating.transitions.TransitionMerged;
import org.gramat.codes.Code;
import org.gramat.inputs.Location;
import org.gramat.logging.Logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

public interface DetEngine {

    static DeterministicMachine run(DeterministicMachine nMachine, Logger logger) {
        var am = new Automaton();
        var am0 = nMachine.am;
        var codes = am0.listCodes();
        var queue = new ArrayDeque<Set<State>>();
        var control = new HashSet<String>();
        var idClosures = new LinkedHashMap<String, Set<State>>();
        var idNewStates = new LinkedHashMap<String, State>();
        var mapState = (BiFunction<Set<State>,String,State>) (closure, id) -> {
            return idNewStates.computeIfAbsent(id, k -> {
                var newState = am.createState(collectLocations(closure));
                idClosures.put(id, closure);
                return newState;
            });
        };

        var closure0 = Set.of(nMachine.initial);

        queue.add(closure0);

        while (!queue.isEmpty()) {
            var sources = queue.remove();
            var sourcesID = State.computeID(sources);
            if (control.add(sourcesID)) {
                var newSource = mapState.apply(sources, sourcesID);

                for (var code : codes) {
                    var transitions = findTransitions(am0, sources, code);

                    if (!transitions.isEmpty()) {
                        var targets = collectTargets(transitions);
                        var targetsID = State.computeID(targets);
                        var newTarget = mapState.apply(targets, targetsID);
                        var beginActions = collectActions(transitions, ActionRole.BEGIN);
                        var endActions = collectActions(transitions, ActionRole.END);

                        am.addMerged(newSource, newTarget, code, beginActions, endActions);

                        queue.add(targets);
                    }
                }
            }
        }

        var newInitial = idNewStates.get(State.computeID(closure0));
        var newAccepteds = new LinkedHashSet<State>();

        for (var entry : idClosures.entrySet()) {
            for (var oldAccepted : nMachine.accepted) {
                if (entry.getValue().contains(oldAccepted)) {
                    var newAccepted = idNewStates.get(entry.getKey());

                    newAccepteds.add(newAccepted);
                }
            }
        }

        return new DeterministicMachine(am, newInitial, newAccepteds);
    }

    static List<ActionTemplate> collectActions(List<TransitionMerged> transitions, ActionRole role) {
        List<ActionTemplate> result = new ArrayList<>();

        for (var t : transitions) {
            if (role == ActionRole.BEGIN) {
                result = ActionEngine.joinTemplates(result, t.beginActions);
            }
            else if (role == ActionRole.END) {
                result = ActionEngine.joinTemplates(result, t.endActions);
            }
            else {
                throw new IllegalStateException();
            }
        }

        return result;
    }

    static List<TransitionMerged> findTransitions(Automaton am, Set<State> closure, Code code) {
        var result = new ArrayList<TransitionMerged>();

        for (var t : am.transitions) {
            if (t instanceof TransitionMerged) {
                var tm = (TransitionMerged) t;

                if (closure.contains(tm.source) && Objects.equals(tm.code, code)) {
                    result.add(tm);
                }
            }
            else {
                throw new RuntimeException();
            }
        }

        return result;
    }

    static Set<State> collectTargets(List<? extends Transition> transitions) {
        var result = new LinkedHashSet<State>();

        for (var t : transitions) {
            result.add(t.target);
        }

        return result;
    }

    static Set<Location> collectLocations(Set<State> states) {
        var result = new LinkedHashSet<Location>();

        for (var state : states) {
            result.addAll(state.locations);
        }

        return result;
    }



}
