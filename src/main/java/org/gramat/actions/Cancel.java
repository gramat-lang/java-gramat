package org.gramat.actions;

public class Cancel implements Action {

    public final Action action;

    public Cancel(Action action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return String.format("cancel:%s", action);
    }
}
