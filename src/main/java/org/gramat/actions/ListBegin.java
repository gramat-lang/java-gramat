package org.gramat.actions;

public class ListBegin implements Action {

    static final ListBegin INSTANCE = new ListBegin();

    private ListBegin() {}

    @Override
    public String toString() {
        return "list-begin";
    }
}
