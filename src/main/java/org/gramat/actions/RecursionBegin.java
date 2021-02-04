package org.gramat.actions;

import org.gramat.automating.Level;
import org.gramat.util.Definition;

public class RecursionBegin extends Action {
    public final String name;
    public final Level level;

    public RecursionBegin(String name, Level level) {
        this.name = name;
        this.level = level;
    }

    @Override
    protected void define(Definition def) {
        def.attr("name", name);
        def.attr("level", level.id);
    }
}
