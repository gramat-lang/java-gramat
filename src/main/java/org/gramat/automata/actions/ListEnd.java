package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.builder.DataBuilder;
import org.gramat.automata.builder.ListEndInstruction;

@Slf4j
public class ListEnd implements Action {

    private final String typeHint;

    private final ListEndInstruction instruction;

    ListEnd(String typeHint) {
        this.typeHint = typeHint;
        this.instruction = new ListEndInstruction(typeHint);
    }

    public String getTypeHint() {
        return typeHint;
    }

    @Override
    public String toString() {
        if (typeHint != null) {
            return String.format("list-end(%s)", typeHint);
        }
        return "list-end()";
    }

    @Override
    public void run(DataBuilder builder) {
        builder.push(instruction);
    }
}
