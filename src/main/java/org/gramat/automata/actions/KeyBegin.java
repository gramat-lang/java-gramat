package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.builder.DataBuilder;
import org.gramat.automata.builder.KeyBeginInstruction;

@Slf4j
public class KeyBegin implements Action {

    private final KeyBeginInstruction instruction;

    KeyBegin() {
        instruction = new KeyBeginInstruction();
    }

    @Override
    public String toString() {
        return "key-begin()";
    }

    @Override
    public void run(DataBuilder builder) {
        builder.push(instruction);
    }
}
