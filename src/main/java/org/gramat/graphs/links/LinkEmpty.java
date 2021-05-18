package org.gramat.graphs.links;

import org.gramat.data.actions.Actions;
import org.gramat.graphs.Node;

public class LinkEmpty extends LinkAction {

    public LinkEmpty(Node source, Node target, Actions beginActions, Actions endActions) {
        super(source, target, beginActions, endActions);
    }

    @Override
    public String toString() {
        return String.format("Link{%s -> %s : empty}", source, target);
    }

}
