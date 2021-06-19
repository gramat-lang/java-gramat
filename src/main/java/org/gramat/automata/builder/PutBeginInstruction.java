package org.gramat.automata.builder;

public class PutBeginInstruction implements DataInstruction {
    @Override
    public void run(DataContext context) {
        context.pushContainer();
    }
}
