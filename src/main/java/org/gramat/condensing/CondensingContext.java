package org.gramat.condensing;

import org.gramat.expressions.Expression;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class CondensingContext {
    private final CondensingPipeline pipeline;
    private final Instant startTime;

    public CondensingContext(CondensingPipeline pipeline) {
        this.pipeline = pipeline;
        this.startTime = Instant.now();
    }

    public Expression condense(Expression expression) {
        return pipeline.apply(expression, this);
    }

    public double getElapsedSeconds() {
        return ChronoUnit.NANOS.between(startTime, Instant.now()) / 1_000_000_000.0;
    }
}
