package org.gramat.automata.builder;

import org.gramat.automata.actions.Action;
import org.gramat.automata.tapes.Tape;
import org.gramat.automata.tokens.Token;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class DataBuilder {

    private final Deque<Token> tokens;
    private final Tape tape;
    private final List<DataInstruction> instructions;

    public DataBuilder(Tape tape) {
        this.tape = tape;
        this.tokens = new ArrayDeque<>();
        this.instructions = new ArrayList<>();
    }

    public Token getToken() {
        return tokens.peek();
    }

    public void run(Action[] actions) {
        for (var action : actions) {
            action.run(this);
        }
    }

    public Object build() {
        var context = new DataContext();

        for (var instruction : instructions) {
            instruction.run(context);
        }

        return context.get();
    }

    public void push(DataInstruction instruction) {
        instructions.add(instruction);
    }

    public void push(Token token) {
        tokens.push(token);
    }

    public void pop(Token token) {
        if (token.matches(tokens.peek())) {
            if (!tokens.isEmpty()) {
                tokens.pop();
            }
        }
        else {
            throw new RuntimeException();
        }
    }

    public int getPosition() {
        return tape.getPosition();
    }
}
