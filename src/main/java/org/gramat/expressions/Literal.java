package org.gramat.expressions;

import org.gramat.location.Location;
import org.gramat.machine.patterns.Pattern;

import java.util.List;

public class Literal extends Expression {

    public final Pattern pattern;

    Literal(Location location, Pattern pattern) {
        super(location);
        this.pattern = pattern;
    }

    @Override
    public List<Expression> getChildren() {
        return List.of();
    }

}
