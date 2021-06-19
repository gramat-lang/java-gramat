package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.builder.DataBuilder;
import org.gramat.automata.builder.KeyEndInstruction;

@Slf4j
public class KeyEnd implements Action {

    private final KeyEndInstruction instruction;

    KeyEnd() {
        instruction = new KeyEndInstruction();
    }

    @Override
    public String toString() {
        return "key-end()";
    }

    @Override
    public void run(DataBuilder builder) {
        builder.push(instruction);
    }
}
