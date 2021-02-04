package org.gramat.codes;

import org.gramat.util.PP;

public class CodeChar implements Code {

    public final char value;

    public CodeChar(char value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Char(" + PP.str(value) + ")";
    }
}
