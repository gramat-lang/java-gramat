package org.gramat.machine.links;

import org.gramat.machine.operations.Operation;
import org.gramat.machine.nodes.Node;
import org.gramat.machine.operations.OperationList;
import org.gramat.machine.patterns.Pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LinkPattern implements Link {

    private final Node source;
    private final Node target;
    private final OperationList beginOperations;
    private final OperationList endOperations;
    private final Pattern pattern;

    LinkPattern(Node source, Node target, Pattern pattern) {
        this.source = Objects.requireNonNull(source);
        this.target = Objects.requireNonNull(target);
        this.pattern = Objects.requireNonNull(pattern);
        this.beginOperations = OperationList.create();
        this.endOperations = OperationList.create();
    }

    @Override
    public Node getSource() { return source; }

    @Override
    public Node getTarget() { return target; }

    @Override
    public OperationList getBeginOperations() { return beginOperations; }

    @Override
    public OperationList getEndOperations() { return endOperations; }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean hasPattern() {
        return true;
    }

    @Override
    public Pattern getPattern() {
        return pattern;
    }
}
