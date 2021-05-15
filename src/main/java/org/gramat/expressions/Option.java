package org.gramat.expressions;

import org.gramat.location.Location;

import java.util.List;

public class Option extends Expression {

    public final Expression content;

    Option(Location location, Expression content) {
        super(location);
        this.content = content;
    }

    @Override
    public List<Expression> getChildren() {
        return List.of(content);
    }

    public Option derive(Expression newContent) {
        if (content == newContent) {
            return this;
        }
        return new Option(location, newContent);
    }
}
