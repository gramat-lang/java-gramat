package org.gramat.automata.builder;

public class KeyBeginInstruction implements DataInstruction {
    @Override
    public void run(DataContext context) {
        context.pushContainer();
    }
}