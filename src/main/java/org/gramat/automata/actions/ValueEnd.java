package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.builder.DataBuilder;
import org.gramat.automata.builder.ValueEndInstruction;

@Slf4j
public class ValueEnd implements Action {
    private final String typeHint;

    ValueEnd(String typeHint) {
        this.typeHint = typeHint;
    }

    public String getTypeHint() {
        return typeHint;
    }

    @Override
    public String toString() {
        if (typeHint != null) {
            return String.format("value-end(%s)", typeHint);
        }
        return "value-end()";
    }

    @Override
    public void run(DataBuilder builder) {
        var position = builder.getPosition();

        builder.push(new ValueEndInstruction(position, typeHint));
    }
}
