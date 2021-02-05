package org.gramat.codes;

import org.gramat.util.PP;

public class CodeRange implements Code {

    public final char begin;
    public final char end;

    public CodeRange(char begin, char end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    public boolean test(char c) {
        return c >= begin && c <= end;
    }

    @Override
    public String toString() {
        return "Range(" + PP.str(begin) + "," + PP.str(end) + ")";
    }
}
