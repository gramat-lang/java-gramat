package org.gramat.machine.links;

import org.gramat.actions.Action;
import org.gramat.actions.Actions;
import org.gramat.machine.nodes.Node;
import org.gramat.patterns.Pattern;

public interface Link {

    Node getSource();
    Node getTarget();

    Actions getBeforeActions();
    Actions getAfterActions();

    boolean isEmpty();
    boolean hasPattern();

    Pattern getPattern();

    void addBeforeActions(Action action);
    void addBeforeActions(Actions actions);
    void addAfterActions(Actions actions);
    void addAfterActions(Action action);

}
