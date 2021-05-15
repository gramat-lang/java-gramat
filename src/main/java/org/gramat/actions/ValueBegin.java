package org.gramat.actions;

public class ValueBegin implements Action {

    static final ValueBegin INSTANCE = new ValueBegin();

    private ValueBegin() {}

    @Override
    public String toString() {
        return "value-begin";
    }
}
