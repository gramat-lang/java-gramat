package org.gramat.actions;

public class ValueEnd implements Action {
    public final String typeHint;

    ValueEnd(String typeHint) {
        this.typeHint = typeHint;
    }

    @Override
    public String toString() {
        if (typeHint != null) {
            return String.format("value-end(%s)", typeHint);
        }
        return "value-end";
    }
}
