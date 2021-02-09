package org.gramat.codes;

import org.gramat.util.PP;

public class CodeChar implements Code {

    public final char value;

    public CodeChar(char value) {
        this.value = value;
    }

    @Override
    public boolean test(char c) {
        return c == value;
    }

    @Override
    public boolean intersects(Code code) {
        if (code instanceof CodeChar) {
            var cc = (CodeChar)code;

            return cc.value == value;
        }
        else if (code instanceof CodeRange) {
            var cr = (CodeRange) code;

            return cr.test(value);
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String toString() {
        return "Char(" + PP.str(value) + ")";
    }
}
