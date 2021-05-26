package org.gramat.patterns;

import org.gramat.tools.PP;

public class PatternRange implements Pattern {
    public final char begin;
    public final char end;

    PatternRange(char begin, char end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    public String toString() {
        return String.format("%s-%s", PP.ch(begin), PP.ch(end));
    }
}
