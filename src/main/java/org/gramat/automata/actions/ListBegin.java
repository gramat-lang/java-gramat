package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.builder.DataBuilder;
import org.gramat.automata.builder.ListBeginInstruction;

@Slf4j
public class ListBegin implements Action {

    private final ListBeginInstruction instruction;

    ListBegin() {
        instruction = new ListBeginInstruction();
    }

    @Override
    public String toString() {
        return "list-begin()";
    }

    @Override
    public void run(DataBuilder builder) {
        builder.push(instruction);
    }
}
