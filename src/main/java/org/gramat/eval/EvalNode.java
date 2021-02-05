package org.gramat.eval;

import java.util.LinkedList;

public class EvalNode {

    public final int id;

    public EvalLink[] links;
    public boolean accepted;

    public EvalNode(int id) {
        this.id = id;
    }
}
