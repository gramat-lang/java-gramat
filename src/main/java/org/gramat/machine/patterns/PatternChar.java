package org.gramat.machine.patterns;

import org.gramat.tools.PP;

public class PatternChar implements Pattern {
    public final char value;

    PatternChar(char value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return PP.ch(value);
    }
}
