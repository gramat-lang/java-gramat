package org.gramat.automata.builder;

public class MapBeginInstruction implements DataInstruction {
    @Override
    public void run(DataContext context) {
        context.pushContainer();
    }
}
