package org.gramat.actions;

import org.gramat.util.Definition;

public class ListEnd extends Action {
    public final String typeHint;

    public ListEnd(String typeHint) {
        this.typeHint = typeHint;
    }

    @Override
    protected void define(Definition def) {
        def.attr("typeHint", typeHint);
    }
}
