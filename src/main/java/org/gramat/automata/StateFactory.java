package org.gramat.automata;

import java.util.ArrayList;
import java.util.List;

public class StateFactory {

    private final List<State> states;

    public StateFactory() {
        states = new ArrayList<>();
    }

    public State createState(boolean accepted) {
        var state = new State(accepted);

        states.add(state);

        return state;
    }

    public int indexOf(State state) {
        return states.indexOf(state);
    }

    public State[] toArray() {
        return states.toArray(new State[0]);
    }
}
