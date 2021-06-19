package org.gramat.automata.builder;

public class ListBeginInstruction implements DataInstruction {
    @Override
    public void run(DataContext context) {
        context.pushContainer();
    }
}
