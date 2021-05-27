package org.gramat.machine.operations;

import java.util.ArrayList;
import java.util.List;

public record Operation(OperationMode mode, OperationType type, int group, String argument) {

    public static List<Operation> join(List<Operation> first, List<Operation> last) {
        var items = new ArrayList<>(first);
        items.addAll(last);
        return items;
    }

    @Override
    public String toString() {
        return String.format(
                "%s-%s(%s)",
                mode.name().toLowerCase(),
                type.name().toLowerCase(),
                argument != null ? argument : "");
    }
}
