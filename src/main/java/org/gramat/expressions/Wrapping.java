package org.gramat.expressions;

import org.gramat.machine.operations.OperationType;
import org.gramat.location.Location;

import java.util.List;

public class Wrapping extends Expression {

    public final OperationType type;
    public final String argument;
    public final Expression content;

    Wrapping(Location location, OperationType type, String argument, Expression content) {
        super(location);
        this.type = type;
        this.argument = argument;
        this.content = content;
    }

    @Override
    public List<Expression> getChildren() {
        return List.of(content);
    }

}
