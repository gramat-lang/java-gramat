package org.gramat.expressions;

import org.gramat.location.Location;

import java.util.List;

public class Sequence extends Expression {

    public final List<Expression> items;

    Sequence(Location location, List<Expression> items) {
        super(location);
        this.items = items;
    }

    @Override
    public List<Expression> getChildren() {
        return items;
    }

    public Sequence derive(List<Expression> newItems) {
        if (items == newItems) {
            return this;
        }
        return new Sequence(location, newItems);  // TODO seal list
    }
}
