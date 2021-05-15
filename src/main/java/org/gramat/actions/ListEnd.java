package org.gramat.actions;

public class ListEnd implements Action {

    public final String typeHint;

    ListEnd(String typeHint) {
        this.typeHint = typeHint;
    }

    @Override
    public String toString() {
        if (typeHint != null) {
            return String.format("list-end(%s)", typeHint);
        }
        return "list-end";
    }

}
