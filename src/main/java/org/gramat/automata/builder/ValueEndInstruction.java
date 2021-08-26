package org.gramat.automata.builder;

public class ValueEndInstruction implements DataInstruction {
    private final int endPosition;
    private final String typeHint;

    public ValueEndInstruction(int endPosition, String typeHint) {
        this.endPosition = endPosition;
        this.typeHint = typeHint;
    }

    @Override
    public void run(DataContext context) {
        var begin = context.popBeginPosition();
        var text = context.getSubstring(begin, endPosition);
        var value = context.parseText(text, typeHint);
        var currentContainer = context.peekContainer();

        currentContainer.addItem(value);
    }
}
