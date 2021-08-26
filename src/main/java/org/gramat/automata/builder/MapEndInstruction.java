package org.gramat.automata.builder;

public class MapEndInstruction implements DataInstruction {
    private final String typeHint;

    public MapEndInstruction(String typeHint) {
        this.typeHint = typeHint;
    }

    @Override
    public void run(DataContext context) {
        var mapContainer = context.popContainer();
        var value = mapContainer.buildMap(typeHint);
        var currentContainer = context.peekContainer();

        currentContainer.addItem(value);
    }
}
