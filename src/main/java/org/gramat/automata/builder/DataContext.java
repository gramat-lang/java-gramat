package org.gramat.automata.builder;

import org.gramat.automata.tapes.Tape;

import java.util.ArrayDeque;
import java.util.Deque;

public class DataContext {

    private final Deque<DataContainer> stack;

    private Integer beginPosition;
    private final Tape tape;

    public DataContext(Tape tape) {
        this.tape = tape;
        this.stack = new ArrayDeque<>();
        this.stack.push(new DataContainer());
    }

    public Object get() {
        var current = stack.pop();
        if (current == null) {
            return null;
        }
        else if (!stack.isEmpty()) {
            throw new RuntimeException();
        }

        return current.buildValue();
    }

    public void pushContainer() {
        stack.push(new DataContainer());
    }

    public DataContainer popContainer() {
        var container = stack.pop();
        if (container == null) {
            throw new RuntimeException();
        }
        return container;
    }

    public DataContainer peekContainer() {
        var container = stack.peek();
        if (container == null) {
            throw new RuntimeException();
        }
        return container;
    }

    public void pushBeginPosition(int position) {
        if (beginPosition != null) {
            throw new RuntimeException();
        }
        this.beginPosition = position;
    }

    public int popBeginPosition() {
        if (beginPosition == null) {
            throw new RuntimeException();
        }
        return beginPosition;
    }

    public String getSubstring(int begin, int end) {
        return tape.substring(begin, end);
    }

    public Object parseText(String text, String typeHint) {
        // TODO type hint
        return text;
    }
}
