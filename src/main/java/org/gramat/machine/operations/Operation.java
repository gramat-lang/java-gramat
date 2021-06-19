package org.gramat.machine.operations;

import java.util.ArrayList;
import java.util.List;

public record Operation(OperationMode mode, OperationType type, String argument) {

    @Override
    public String toString() {
        return String.format(
                "%s-%s(%s)",
                mode.name().toLowerCase(),
                type.name().toLowerCase(),
                argument != null ? argument : "");
    }
}
