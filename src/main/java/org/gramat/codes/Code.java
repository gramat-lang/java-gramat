package org.gramat.codes;

public interface Code {
    boolean test(char c);

    boolean intersects(Code code);
}
