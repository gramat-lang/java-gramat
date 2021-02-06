package org.gramat.actions;

import org.gramat.eval.EvalEngine;
import org.gramat.util.Definition;

public class TextEnd extends Action {
    public final String parser;

    public TextEnd(int id, String parser) {
        super(id);
        this.parser = parser;
    }

    @Override
    public void execute(EvalEngine engine) {
        var end = engine.input.getPosition();

        engine.builder.performPopText(id, end, parser);
    }

    @Override
    protected void define(Definition def) {
        def.attr("parser", parser);
    }
}
