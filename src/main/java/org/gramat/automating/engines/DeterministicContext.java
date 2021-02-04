package org.gramat.automating.engines;

import org.gramat.automating.Automaton;
import org.gramat.automating.State;
import org.gramat.automating.StateSet;
import org.gramat.exceptions.GramatException;

import java.util.LinkedHashMap;
import java.util.Map;

public class DeterministicContext {
    public final Map<String, StateSet> idClosures;
    public final Map<String, State> idStates;
    public final Automaton result;

    public DeterministicContext(Automaton result) {
        this.idClosures = new LinkedHashMap<>();
        this.idStates = new LinkedHashMap<>();
        this.result = result;
    }

    public boolean containsClosureID(String id) {
        return idStates.containsKey(id);
    }

    public State mapClosure(StateSet closure) {
        return idStates.computeIfAbsent(closure.getID(), closureID -> {
            var state = result.createState();

            idClosures.put(closureID, closure);

            return state;
        });
    }

    public State unmapClosure(StateSet closure) {
        var state = idStates.get(closure.getID());
        if (state == null) {
            throw new GramatException("state not found: " + closure.getID());
        }
        return state;
    }

    public StateSet unmapWith(State end) {
        var result = StateSet.of();

        for (var closure : idClosures.values()) {
            if (closure.contains(end)) {
                var state = unmapClosure(closure);

                result.add(state);
            }
        }

        return result;
    }
}

