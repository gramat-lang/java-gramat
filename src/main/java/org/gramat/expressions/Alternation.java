package org.gramat.expressions;

import org.gramat.location.Location;

import java.util.List;

public class Alternation extends Expression {

    public final List<Expression> items;

    Alternation(Location location, List<Expression> items) {
        super(location);
        this.items = items;
    }

    @Override
    public List<Expression> getChildren() {
        return items;
    }

    public Alternation derive(List<Expression> newItems) {
        if (items == newItems) {
            return this;
        }
        return new Alternation(location, newItems);  // TODO seal list
    }
}
