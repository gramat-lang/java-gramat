package org.gramat.actions;

import org.gramat.eval.EvalEngine;
import org.gramat.util.Definition;

public class HeapPush extends Action {

    public final int level;

    public HeapPush(int level) {
        this.level = level;
    }

    @Override
    public void execute(EvalEngine engine) {
        // TODO
    }

    @Override
    protected void define(Definition def) {
        def.attr("level", level);
    }
}
