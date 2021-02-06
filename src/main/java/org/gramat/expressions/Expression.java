package org.gramat.expressions;

import org.gramat.inputs.Location;
import org.gramat.util.DefinedObject;

import java.util.List;
import java.util.Objects;

public abstract class Expression extends DefinedObject {

    public final Location beginLocation;
    public final Location endLocation;

    protected Expression(Location beginLocation, Location endLocation) {
        this.beginLocation = Objects.requireNonNull(beginLocation);
        this.endLocation = Objects.requireNonNull(endLocation);
    }

    public abstract List<Expression> getChildren();

}
