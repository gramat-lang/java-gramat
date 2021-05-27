package org.gramat.machine.links;

import org.gramat.machine.operations.Operation;
import org.gramat.machine.nodes.Node;
import org.gramat.machine.patterns.Pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LinkPattern implements Link {

    private final Node source;
    private final Node target;
    private final List<Operation> beginOperations;
    private final List<Operation> endOperations;
    private final Pattern pattern;

    LinkPattern(Node source, Node target, Pattern pattern) {
        this.source = Objects.requireNonNull(source);
        this.target = Objects.requireNonNull(target);
        this.pattern = Objects.requireNonNull(pattern);
        this.beginOperations = new ArrayList<>();
        this.endOperations = new ArrayList<>();
    }

    @Override
    public Node getSource() { return source; }

    @Override
    public Node getTarget() { return target; }

    @Override
    public List<Operation> getBeginOperations() { return beginOperations; }

    @Override
    public List<Operation> getEndOperations() { return endOperations; }

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

    public void prependBeginOperation(Operation operation) {
        beginOperations.add(0, operation);
    }

    public void prependBeginOperations(List<Operation> operations) {
        beginOperations.addAll(0, operations);
    }

    public void appendEndOperation(Operation operation) {
        endOperations.add(operation);
    }

    public void appendEndOperations(List<Operation> operations) {
        endOperations.addAll(operations);
    }
}
