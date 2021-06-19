package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.builder.DataBuilder;
import org.gramat.automata.builder.PutBeginInstruction;

@Slf4j
public class PutBegin implements Action {

    private final PutBeginInstruction instruction;

    PutBegin() {
        instruction = new PutBeginInstruction();
    }

    @Override
    public String toString() {
        return "put-begin()";
    }

    @Override
    public void run(DataBuilder builder) {
        builder.push(instruction);
    }
}
