package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.builder.DataBuilder;
import org.gramat.automata.builder.PutEndInstruction;

@Slf4j
public class PutEnd implements Action {
    private final String keyHint;
    private final PutEndInstruction instruction;

    PutEnd(String keyHint) {
        this.keyHint = keyHint;
        this.instruction = new PutEndInstruction(keyHint);
    }

    public String getKeyHint() {
        return keyHint;
    }

    @Override
    public String toString() {
        if (keyHint != null) {
            return String.format("put-end(%s)", keyHint);
        }
        return "put-end()";
    }

    @Override
    public void run(DataBuilder builder) {
        builder.push(instruction);
    }
}
