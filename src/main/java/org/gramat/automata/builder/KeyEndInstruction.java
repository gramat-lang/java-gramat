package org.gramat.automata.builder;

public class KeyEndInstruction implements DataInstruction {
    @Override
    public void run(DataContext context) {
        var keyContainer = context.popContainer();
        var key = keyContainer.buildString();
        var currentContainer = context.peekContainer();

        currentContainer.setKey(key);
    }
}
