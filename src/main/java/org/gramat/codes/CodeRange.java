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
    public boolean intersects(Code code) {
        if (code instanceof CodeChar) {
            var cc = (CodeChar)code;

            return cc.value >= begin && cc.value <= end;
        }
        else if (code instanceof CodeRange) {
            var cr = (CodeRange) code;

            return (cr.begin >= begin && cr.begin <= end || cr.end >= begin && cr.end <= end);
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String toString() {
        return "Range(" + PP.str(begin) + "," + PP.str(end) + ")";
    }
}
