package org.gramat.actions;

import org.gramat.eval.EvalEngine;
import org.gramat.util.Definition;

public class MetadataBegin extends Action {

    public MetadataBegin(int id) {
        super(id);
    }

    @Override
    public void execute(EvalEngine engine) {
        engine.builder.performPushMetadata(id);
    }

    @Override
    protected void define(Definition def) {
        // nothing more
    }
}
