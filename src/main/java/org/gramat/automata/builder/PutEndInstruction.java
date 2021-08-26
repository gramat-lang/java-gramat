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
        var key = resolveKey(putContainer.getKey());

        var currentContainer = context.peekContainer();

        currentContainer.addPair(key, value);
    }

    private String resolveKey(String key) {
        if (key != null && keyHint != null) {
            throw new RuntimeException("conflicting key");
        }
        else if (key == null) {
            if (keyHint == null) {
                throw new RuntimeException("missing key");
            }
            return keyHint;
        }
        return key;
    }
}
