package org.gramat.actions;

public class Ignore implements Action {

    public final Action action;

    public Ignore(Action action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return String.format("ignore:%s", action);
    }
}
