package org.gramat.graphs;

import org.gramat.data.Actions;
import org.gramat.data.ActionsW;

public class LinkAction extends Link {

    public final ActionsW beginActions;
    public final ActionsW endActions;

    protected LinkAction(Node source, Node target, Actions beginActions, Actions endActions) {
        super(source, target);
        this.beginActions = Actions.copyW(beginActions);
        this.endActions = Actions.copyW(endActions);
    }
}
