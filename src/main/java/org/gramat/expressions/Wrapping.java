package org.gramat.expressions;

import org.gramat.actions.ActionType;
import org.gramat.location.Location;

import java.util.List;

public class Wrapping extends Expression {

    public final ActionType type;
    public final String argument;
    public final Expression content;

    Wrapping(Location location, ActionType type, String argument, Expression content) {
        super(location);
        this.type = type;
        this.argument = argument;
        this.content = content;
    }

    @Override
    public List<Expression> getChildren() {
        return List.of(content);
    }

    public Wrapping derive(Expression newContent) {
        if (content == newContent) {
            return this;
        }
        return new Wrapping(location, type, argument, newContent);
    }
}
