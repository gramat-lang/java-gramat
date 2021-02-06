package org.gramat.actions;

import org.gramat.eval.EvalEngine;
import org.gramat.util.Definition;

public class TextBegin extends Action {

    public TextBegin(int id) {
        super(id);
    }

    @Override
    public void execute(EvalEngine engine) {
        var begin = engine.input.getPosition();

        engine.builder.performPushText(id, begin);
    }
    @Override
    protected void define(Definition def) {
        // nothing more
    }
}
