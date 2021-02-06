package org.gramat.eval;

import org.gramat.tracking.SourceMap;

import java.util.Objects;

public class EvalProgram {

    public final EvalNode node;
    public final SourceMap sourceMap;

    public EvalProgram(EvalNode node, SourceMap sourceMap) {
        this.node = Objects.requireNonNull(node);
        this.sourceMap = Objects.requireNonNull(sourceMap);
    }
}
