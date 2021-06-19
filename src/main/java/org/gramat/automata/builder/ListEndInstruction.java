package org.gramat.automata.builder;

public class ListEndInstruction implements DataInstruction {

    private final String typeHint;

    public ListEndInstruction(String typeHint) {
        this.typeHint = typeHint;
    }

    @Override
    public void run(DataContext context) {
        var listContainer = context.popContainer();
        var value = listContainer.buildList(typeHint);
        var currentContainer = context.peekContainer();

        currentContainer.add(value);
    }
}
