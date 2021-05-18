package org.gramat.graphs.links;

import org.gramat.data.actions.Actions;
import org.gramat.data.actions.ActionsW;
import org.gramat.graphs.Node;

public abstract class LinkAction extends Link {

    public final ActionsW beginActions;
    public final ActionsW endActions;

    protected LinkAction(Node source, Node target, Actions beginActions, Actions endActions) {
        super(source, target);
        this.beginActions = Actions.copyW(beginActions);
        this.endActions = Actions.copyW(endActions);
    }
}
