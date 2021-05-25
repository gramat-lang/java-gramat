package org.gramat.graphs.links;

import org.gramat.actions.Action;
import org.gramat.data.actions.Actions;
import org.gramat.graphs.Node;
import org.gramat.symbols.Symbol;

public interface Link {

    Node getSource();
    Node getTarget();

    Actions getBeforeActions();
    Actions getAfterActions();

    boolean isEmpty();
    boolean hasSymbol();

    Symbol getSymbol();

    void addBeforeActions(Action action);
    void addBeforeActions(Actions actions);
    void addAfterActions(Actions actions);
    void addAfterActions(Action action);

}
