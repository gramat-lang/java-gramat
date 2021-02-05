package org.gramat.actions;

import org.gramat.automating.Level;
import org.gramat.eval.EvalEngine;
import org.gramat.util.Definition;

public class RecursionEnd extends Action {
    public final String name;
    public final Level level;

    public RecursionEnd(String name, Level level) {
        this.name = name;
        this.level = level;
    }

    @Override
    public void execute(EvalEngine engine) {
        // TODO
    }

    @Override
    protected void define(Definition def) {
        def.attr("name", name);
        def.attr("level", level.id);
    }
}
