package org.gramat.graphs.links;

import org.gramat.data.actions.Actions;
import org.gramat.data.actions.ActionsW;
import org.gramat.graphs.Node;

import java.util.Objects;

public abstract class Link {

    public final String ids;
    public final Node source;
    public final Node target;
    public final ActionsW beforeActions;
    public final ActionsW afterActions;

    protected Link(String ids, Node source, Node target) {
        this.ids = ids;
        this.source = Objects.requireNonNull(source);
        this.target = Objects.requireNonNull(target);
        this.beforeActions = Actions.createW();
        this.afterActions = Actions.createW();
    }
}
