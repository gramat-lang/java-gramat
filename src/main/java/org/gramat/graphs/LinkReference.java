package org.gramat.graphs;

import org.gramat.data.Actions;
import org.gramat.tools.Validations;


public class LinkReference extends LinkAction {

    public final String name;
    public final String token;

    public LinkReference(Node source, Node target, Actions beginActions, Actions endActions, String name, String token) {
        super(source, target, beginActions, endActions);
        this.name = Validations.notEmpty(name);
        this.token = token;
    }

}
