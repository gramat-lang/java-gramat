package org.gramat.automata.builder;

public class PutEndInstruction implements DataInstruction {
    private final String keyHint;

    public PutEndInstruction(String keyHint) {
        this.keyHint = keyHint;
    }

    @Override
    public void run(DataContext context) {
        var mapContainer = context.popContainer();
        var value = mapContainer.buildValue();
        var currentContainer = context.peekContainer();
        var key = currentContainer.getKey();

        if (key == null && keyHint == null) {
            throw new RuntimeException("missing key");
        }
        else if (key != null && keyHint != null) {
            throw new RuntimeException("conflicting key");
        }
        else if (key == null) {
            key = keyHint;
        }

        currentContainer.set(key, value);
    }
}
