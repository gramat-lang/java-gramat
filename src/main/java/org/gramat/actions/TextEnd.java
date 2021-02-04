package org.gramat.actions;

import org.gramat.util.Definition;

public class TextEnd extends Action {
    public final String parser;

    public TextEnd(String parser) {
        this.parser = parser;
    }

    @Override
    protected void define(Definition def) {
        def.attr("parser", parser);
    }
}
