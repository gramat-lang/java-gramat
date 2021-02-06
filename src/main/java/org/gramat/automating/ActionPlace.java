package org.gramat.automating;

import org.gramat.actions.Action;
import org.gramat.inputs.Location;

import java.util.Set;

public class ActionPlace {
    public final Action action;
    public final Set<Location> locations;

    public ActionPlace(Action action, Set<Location> locations) {
        this.action = action;
        this.locations = locations;
    }
}
