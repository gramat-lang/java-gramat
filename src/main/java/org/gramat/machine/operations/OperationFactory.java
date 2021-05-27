package org.gramat.machine.operations;

import org.gramat.tools.IdentifierProvider;

public class OperationFactory {

    private final IdentifierProvider groupNumbers;

    public OperationFactory() {
        groupNumbers = IdentifierProvider.create(1);
    }

    public int nextGroup() {
        return groupNumbers.next();
    }

    public Operation createBegin(OperationType type, int group, String argument) {
        return new Operation(OperationMode.BEGIN, type, group, argument);
    }

    public Operation createEnd(OperationType type, int group, String argument) {
        return new Operation(OperationMode.END, type, group, argument);
    }

}
