package org.gramat.expressions;

import org.gramat.location.Location;
import org.gramat.tools.Validations;

import java.util.List;

public class Reference extends Expression {

    public final String name;

    Reference(Location location, String name) {
        super(location);

        this.name = Validations.notEmpty(name);
    }

    @Override
    public List<Expression> getChildren() {
        return List.of();
    }
}
