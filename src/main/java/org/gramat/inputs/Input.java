package org.gramat.inputs;

public interface Input {
    char peek();

    char pull();

    boolean alive();

    Position position();
}
