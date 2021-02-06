package org.gramat.util;

public abstract class DefinedObject {

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
        if (o instanceof DefinedObject) {
            var that = (DefinedObject)o;
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
}
