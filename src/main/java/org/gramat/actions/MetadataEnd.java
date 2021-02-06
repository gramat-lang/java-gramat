package org.gramat.actions;

import org.gramat.eval.EvalEngine;
import org.gramat.util.Definition;

import java.util.Objects;

public class MetadataEnd extends Action {

    public final String name;

    public MetadataEnd(int id, String name) {
        super(id);
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public void execute(EvalEngine engine) {
        engine.builder.performPopMetadata(id, name);
    }

    @Override
    protected void define(Definition def) {
        def.attr("name", name);
    }
}
