package org.gramat.graphs;

import org.gramat.actions.Action;
import org.gramat.tools.DataUtils;

import java.util.Set;

public class LinkAction extends Link {

    public final Set<Action> beginActions;
    public final Set<Action> endActions;

    protected LinkAction(Node source, Node target, Set<Action> beginActions, Set<Action> endActions) {
        super(source, target);
        this.beginActions = DataUtils.mutableCopy(beginActions);
        this.endActions = DataUtils.mutableCopy(endActions);
    }
}
