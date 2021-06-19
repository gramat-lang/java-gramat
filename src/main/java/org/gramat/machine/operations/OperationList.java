package org.gramat.machine.operations;

import java.util.List;

public interface OperationList extends Iterable<Operation> {

    static OperationList empty() {
        return OperationListNull.INSTANCE;
    }

    static OperationList create() {
        return new OperationListLive();
    }

    static OperationList join(OperationList begin, OperationList end) {
        return new OperationListLive(begin, end);
    }

    void prepend(Operation operation);

    void prepend(OperationList operations);

    void append(Operation operation);

    void append(OperationList operations);

    int size();

    Operation get(int index);

    boolean isPresent();

    List<Operation> toList();
}
