package org.gramat.makers;

public class IntegerMaker implements ValueMaker {
    @Override
    public Object make(String text) {
        return Integer.parseInt(text);
    }
}
