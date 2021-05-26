package org.gramat.machine.links;

import org.gramat.actions.Action;
import org.gramat.actions.Actions;
import org.gramat.machine.nodes.Node;
import org.gramat.symbols.Symbol;

public class LinkEmpty implements Link {

    private final Node source;
    private final Node target;

    LinkEmpty(Node source, Node target) {
        this.source = source;
        this.target = target;
    }

    public Node getSource() { return source; }
    public Node getTarget() { return target; }

    public Actions getBeforeActions() { return Actions.empty(); }
    public Actions getAfterActions() { return Actions.empty(); }

    public boolean isEmpty() {
        return true;
    }

    public boolean hasSymbol() {
        return false;
    }

    public Symbol getSymbol() {
        throw new RuntimeException();
    }

    public void addBeforeActions(Action action) {
        throw new RuntimeException();
    }

    public void addBeforeActions(Actions actions) {
        throw new RuntimeException();
    }

    public void addAfterActions(Actions actions) {
        throw new RuntimeException();
    }

    public void addAfterActions(Action action) {
        throw new RuntimeException();
    }
}
