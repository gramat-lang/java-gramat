package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.builder.DataBuilder;
import org.gramat.automata.builder.MapEndInstruction;

@Slf4j
public class MapEnd implements Action {

    private final String typeHint;

    private final MapEndInstruction instruction;

    MapEnd(String typeHint) {
        this.typeHint = typeHint;
        this.instruction = new MapEndInstruction(typeHint);
    }

    public String getTypeHint() {
        return typeHint;
    }

    @Override
    public String toString() {
        if (typeHint != null) {
            return String.format("map-end(%s)", typeHint);
        }
        return "map-end()";
    }

    @Override
    public void run(DataBuilder builder) {
        builder.push(instruction);
    }
}
