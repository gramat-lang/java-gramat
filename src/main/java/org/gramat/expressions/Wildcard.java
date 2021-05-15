package org.gramat.expressions;

import org.gramat.location.Location;

import java.util.List;

public class Wildcard extends Expression {

    public final int level;

    Wildcard(Location location, int level) {
        super(location);
        this.level = level;
    }

    @Override
    public List<Expression> getChildren() {
        return List.of();
    }
}
