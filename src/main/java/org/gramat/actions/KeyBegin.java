package org.gramat.actions;

public class KeyBegin implements Action {

    static final KeyBegin INSTANCE = new KeyBegin();

    private KeyBegin() {}

    @Override
    public String toString() {
        return "key-begin";
    }

}
