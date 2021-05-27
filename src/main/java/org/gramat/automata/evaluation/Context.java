package org.gramat.automata.evaluation;

import org.gramat.automata.actions.Action;
import org.gramat.automata.tapes.Tape;
import org.gramat.automata.tokens.Token;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class Context {

    private final Deque<Token> stack;

    public Context() {
        this.stack = new ArrayDeque<>();
    }

    public void push(Token token) {
        stack.push(token);
    }

    public void pop(Token token) {
        if (!Objects.equals(stack.pop(), token)) {
            throw new RejectedException("TOKEN", null, null);
        }
    }

    public Token getToken() {
        return stack.peek();
    }

    public void run(Tape tape, Action[] actions) {
        for (var action : actions) {
            action.run(tape, this);
        }
    }

    public Object getResult() {
        return null;
    }

}
