package org.gramat.eval;

import org.gramat.exceptions.GramatException;
import org.gramat.util.PP;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class EvalHeap {

    public static final int ANY = 0;

    private final Deque<Object> stack;

    public EvalHeap() {
        stack = new ArrayDeque<>();
    }

    public void push(int actionID, Object token) {
        stack.push(token);
    }

    public void pop(int actionID, Object token) {
        var actual = stack.pop();
        if (!Objects.equals(actual, token)) {
            throw new GramatException("rejected: wrong level, expected=" + token + ", actual=" + actual + ", stack=" + PP.str(stack));
        }
    }

}
