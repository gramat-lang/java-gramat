package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.builder.DataBuilder;
import org.gramat.automata.builder.ValueBeginInstruction;

@Slf4j
public class ValueBegin implements Action {

    @Override
    public String toString() {
        return "value-begin()";
    }

    @Override
    public void run(DataBuilder builder) {
        var position = builder.getPosition();

        builder.push(new ValueBeginInstruction(position));
    }
}
