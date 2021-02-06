package org.gramat.automating;

import org.gramat.inputs.Location;

import java.util.Collections;
import java.util.Set;

public class State {

    public final Set<Location> locations;
    public final Automaton am;
    public final int id;
    public final boolean wild;

    public State(Automaton am, int id, boolean wild, Set<Location> locations) {
        this.am = am;
        this.id = id;
        this.wild = wild;
        this.locations = Collections.unmodifiableSet(locations);
    }

}
