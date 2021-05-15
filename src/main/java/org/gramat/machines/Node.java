package org.gramat.machines;

public class Node {

    public final int id;

    public boolean wildcard;

    public Node(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", wildcard ? "Wild" : "Node", id);
    }
}
