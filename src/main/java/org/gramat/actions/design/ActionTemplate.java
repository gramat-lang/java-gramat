package org.gramat.actions.design;

import org.gramat.inputs.Location;

import java.util.Set;

public class ActionTemplate {
    public final Set<Location> locations;  // TODO change to a single object?
    public final ActionScheme scheme;
    public final ActionRole role;
    public final int ordinal;
    public final String argument;

    public ActionTemplate(Set<Location> locations, ActionScheme scheme, ActionRole role, int ordinal, String argument) {
        this.locations = locations;
        this.scheme = scheme;
        this.role = role;
        this.ordinal = ordinal;
        this.argument = argument;
    }
}
