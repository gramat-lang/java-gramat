package org.gramat.inputs;

public interface Input {
    char peek();

    char pull();

    boolean alive();

    Location getLocation();  // TODO rename

    String segment(int begin, int end);

    int getPosition();
}
