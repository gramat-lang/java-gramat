package org.gramat.machine.operations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


class OperationListLive implements OperationList {

    private final ArrayList<Operation> items;

    public OperationListLive() {
        items = new ArrayList<>();
    }

    public OperationListLive(OperationList begin, OperationList end) {
        if (begin instanceof OperationListLive beginList) {
            items = new ArrayList<>(beginList.items);
            append(end);
        }
        else {
            items = new ArrayList<>();
            append(begin);
            append(end);
        }
    }

    private List<Operation> keepMissing(List<Operation> operations) {
        return operations.stream()
                .filter(o -> !items.contains(o))
                .collect(Collectors.toList());
    }

    private void interruptOnCollisions(List<Operation> operations) {
        // TODO check for collisions here and throw an exception
    }

    private void prependList(List<Operation> operations) {
        var missing = keepMissing(operations);

        interruptOnCollisions(missing);

        items.addAll(0, missing);
    }

    private void appendList(List<Operation> operations) {
        var missing = keepMissing(operations);

        interruptOnCollisions(missing);

        items.addAll(missing);
    }

    @Override
    public void prepend(Operation operation) {
        prependList(List.of(operation));
    }

    @Override
    public void prepend(OperationList operations) {
        prependList(operations.toList());
    }

    @Override
    public void append(Operation operation) {
        appendList(List.of(operation));
    }

    @Override
    public void append(OperationList operations) {
        appendList(operations.toList());
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public Operation get(int index) {
        return items.get(index);
    }

    @Override
    public boolean isPresent() {
        return !items.isEmpty();
    }

    @Override
    public List<Operation> toList() {
        return new ArrayList<>(items);
    }

    @Override
    public Iterator<Operation> iterator() {
        return items.iterator();
    }
}
