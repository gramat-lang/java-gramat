package org.gramat.machine.operations;

import org.gramat.tools.IdentifierProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OperationFactory {

    private final List<Operation> operations;

    public OperationFactory() {
        operations = new ArrayList<>();
    }

    public Operation create(OperationMode mode, OperationType type, String argument) {
        for (var op : operations) {
            if (op.mode() == mode && op.type() == type && Objects.equals(op.argument(), argument)) {
                return op;
            }
        }

        var op = new Operation(mode, type, argument);

        operations.add(op);

        return op;
    }

    public Operation createBegin(OperationType type, String argument) {
        return create(OperationMode.BEGIN, type, argument);
    }

    public Operation createEnd(OperationType type, String argument) {
        return create(OperationMode.END, type, argument);
    }

}
