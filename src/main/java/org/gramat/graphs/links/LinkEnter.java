package org.gramat.graphs.links;

import org.gramat.data.actions.Actions;
import org.gramat.graphs.Node;

public class LinkEnter extends LinkAction {
    public final String token;

    // TODO should end actions be available?

    public LinkEnter(Node source, Node target, Actions beginActions, Actions endActions, String token) {
        super(source, target, beginActions, endActions);
        this.token = token;
    }

    @Override
    public String toString() {
        return String.format("Link{%s -> %s : enter(%s)}", source, target, token);
    }
}
