package org.gramat.machines;

import org.gramat.actions.Action;
import org.gramat.tools.Validations;

import java.util.Set;

public class LinkReference extends LinkAction {

    public final String name;
    public final String token;

    public LinkReference(Node source, Node target, Set<Action> beginActions, Set<Action> endActions, String name, String token) {
        super(source, target, beginActions, endActions);
        this.name = Validations.notEmpty(name);
        this.token = token;
    }

}
