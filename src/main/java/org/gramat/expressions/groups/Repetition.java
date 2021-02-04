package org.gramat.expressions.groups;

import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionContent;
import org.gramat.util.Definition;

import java.util.Objects;

public class Repetition extends ExpressionContent {

    public final Expression content;

    public Repetition(Expression content) {
        this.content = Objects.requireNonNull(content);
    }

    @Override
    protected void define(Definition def) {
        def.attr("content", content);
    }

    @Override
    public Expression getContent() {
        return content;
    }
}
