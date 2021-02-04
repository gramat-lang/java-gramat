package org.gramat.actions;

import org.gramat.util.Definition;

public class PropertyEnd extends Action {
    public final String nameHint;

    public PropertyEnd(String nameHint) {
        this.nameHint = nameHint;
    }

    @Override
    protected void define(Definition def) {
        def.attr("nameHint", nameHint);
    }
}
