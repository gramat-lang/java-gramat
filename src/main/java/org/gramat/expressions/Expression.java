package org.gramat.expressions;

import org.gramat.exceptions.GramatException;
import org.gramat.inputs.Location;
import org.gramat.runtime.RExpression;
import org.gramat.util.Definition;
import org.gramat.util.WalkControl;
import org.gramat.util.WalkFunction;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class Expression {

    public final Set<Location> locations;

    protected Expression() {
        locations = new LinkedHashSet<>();
    }

    public abstract List<Expression> getChildren();

    protected abstract void define(Definition def);

    private Definition definition() {
        var def = new Definition(getClass());

        define(def);

        return def;
    }

    @Override
    public final String toString() {
        return definition().computeString();
    }

    @Override
    public final boolean equals(Object o) {
        if (o instanceof Expression) {
            var that = (Expression)o;
            var thisDef = this.definition();
            var thatDef = that.definition();

            return thisDef.equals(thatDef);
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return definition().hashCode();
    }

    public RExpression build() {
        throw new GramatException("not implemented at " + getClass());
    }

}
