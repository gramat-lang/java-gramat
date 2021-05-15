package org.gramat.expressions;

import org.gramat.location.Location;
import org.gramat.symbols.Symbol;

import java.util.List;

public class Literal extends Expression {

    public final Symbol symbol;

    Literal(Location location, Symbol symbol) {
        super(location);
        this.symbol = symbol;
    }

    @Override
    public List<Expression> getChildren() {
        return List.of();
    }

}
