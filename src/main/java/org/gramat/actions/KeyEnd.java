package org.gramat.actions;

public class KeyEnd implements Action {

    static final KeyEnd INSTANCE = new KeyEnd();

    private KeyEnd() {}

    @Override
    public String toString() {
        return "key-end";
    }
}
