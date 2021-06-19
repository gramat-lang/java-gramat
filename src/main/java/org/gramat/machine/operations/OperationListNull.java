package org.gramat.machine.operations;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class OperationListNull implements OperationList {

    public static final OperationListNull INSTANCE = new OperationListNull();

    private OperationListNull() {
        // nothing to do
    }

    @Override
    public void prepend(Operation operation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void prepend(OperationList operations) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void append(Operation operation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void append(OperationList operations) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Operation get(int index) {
        throw new IndexOutOfBoundsException(index);
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public List<Operation> toList() {
        return List.of();
    }

    @Override
    public Iterator<Operation> iterator() {
        return Collections.emptyIterator();
    }
}
