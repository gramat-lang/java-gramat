package org.gramat.machine.links;

import org.gramat.machine.operations.Operation;
import org.gramat.machine.nodes.Node;
import org.gramat.machine.operations.OperationList;
import org.gramat.machine.patterns.Pattern;

import java.util.List;

public class LinkEmpty implements Link {

    private final Node source;
    private final Node target;

    LinkEmpty(Node source, Node target) {
        this.source = source;
        this.target = target;
    }

    public Node getSource() { return source; }
    public Node getTarget() { return target; }

    public OperationList getBeginOperations() { return OperationList.empty(); }
    public OperationList getEndOperations() { return OperationList.empty(); }

    public boolean isEmpty() {
        return true;
    }

    public boolean hasPattern() {
        return false;
    }

    public Pattern getPattern() {
        throw new RuntimeException();
    }
}
