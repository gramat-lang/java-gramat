package org.gramat.actions;

public class PutBegin implements Action {

    static final PutBegin INSTANCE = new PutBegin();

    private PutBegin() {}

    @Override
    public String toString() {
        return "put-begin";
    }
}
