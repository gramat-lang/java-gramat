package org.gramat.expressions;

import org.gramat.location.Location;

import java.util.List;

public abstract class Expression {

    public abstract List<Expression> getChildren();

    public final Location location;

    protected Expression(Location location) {
        this.location = location;
    }

}
