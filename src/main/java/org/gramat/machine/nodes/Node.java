package org.gramat.machine.nodes;

public class Node {

    public final int id;

    public boolean wildcard;

    Node(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", wildcard ? "Wild" : "Node", id);
    }
}
