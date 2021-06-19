package org.gramat.automata.builder;

public class PutEndInstruction implements DataInstruction {
    private final String keyHint;

    public PutEndInstruction(String keyHint) {
        this.keyHint = keyHint;
    }

    @Override
    public void run(DataContext context) {
        var putContainer = context.popContainer();
        var value = putContainer.buildValue();
        var key = putContainer.getKey();

        if (key == null && keyHint == null) {
            throw new RuntimeException("missing key");
        }
        else if (key != null && keyHint != null) {
            throw new RuntimeException("conflicting key");
        }
        else if (key == null) {
            key = keyHint;
        }

        var currentContainer = context.peekContainer();

        currentContainer.set(key, value);
    }
}
