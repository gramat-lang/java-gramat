package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.builder.DataBuilder;
import org.gramat.automata.builder.MapBeginInstruction;

@Slf4j
public class MapBegin implements Action {

    private final MapBeginInstruction instruction;

    MapBegin() {
        instruction = new MapBeginInstruction();
    }

    @Override
    public String toString() {
        return "map-begin()";
    }

    @Override
    public void run(DataBuilder builder) {
        builder.push(instruction);
    }
}
