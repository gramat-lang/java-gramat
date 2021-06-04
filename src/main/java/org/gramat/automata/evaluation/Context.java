package org.gramat.automata.evaluation;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.State;
import org.gramat.automata.actions.Action;
import org.gramat.automata.actions.ActionMode;
import org.gramat.automata.actions.ActionType;
import org.gramat.automata.containers.Container;
import org.gramat.automata.containers.ContainerKey;
import org.gramat.automata.containers.ContainerPut;
import org.gramat.automata.messages.Message;
import org.gramat.automata.tapes.Tape;
import org.gramat.automata.tokens.Token;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

@Slf4j
public class Context {

    private final Tape tape;
    private final Deque<Token> stack;
    private final DataBuilder dataBuilder;

    private Message buffer;

    private State state;

    public Context(Tape tape) {
        this.tape = tape;
        this.stack = new ArrayDeque<>();
        this.dataBuilder = new DataBuilder();
    }

    public int getPosition() {
        return tape.getPosition();
    }

    public Token getToken() {
        return stack.peek();
    }

    public void run(Action[] actions) {
        for (var action : actions) {
            run(action);
        }
    }

    private void run(Action action) {
        if (action.getType() == ActionType.TOKEN) {
            if (action.getMode() == ActionMode.BEGIN) {
                push(action.getToken());
            }
            else if (action.getMode() == ActionMode.END) {
                pop(action.getToken());
            }
            else {
                throw new IllegalStateException();
            }
        }
        else if (buffer != null
                && buffer.getGroup() == action.getGroup()
                && buffer.getType() == action.getType()
                && buffer.getMode() == action.getMode()) {
            buffer.setState(state);
            if (buffer.getMode() == ActionMode.BEGIN) {
                // Execute and keep it in buffer,
                //   the message can be executed first time
                //   and ignored subsequent times.
                execute(buffer);
            }
            else if (buffer.getMode() == ActionMode.END) {
                // Do not execute it, only keep it in buffer,
                //   the message is updated multiple times
                //   and executed before next message.
                if (action.hasKeyHint()) {
                    buffer.setKeyHint(action.getKeyHint());
                }
                if (action.hasTypeHint()) {
                    buffer.setTypeHint(action.getTypeHint());
                }
                if (buffer.hasPosition()) {
                    buffer.setPosition(tape.getPosition());
                }
            }
            else {
                throw new IllegalStateException();
            }
        }
        else {
            if (buffer != null) {
                execute(buffer);
            }

            buffer = action.createMessage(this);
        }
    }

    public void push(Token token) {
        stack.push(token);
    }

    public void pop(Token token) {
        if (!Objects.equals(stack.pop(), token)) {
            throw new RejectedException("TOKEN", tape.getLocation(), state);
        }
    }

    protected void execute(Message message) {
        if (message.isPending()) {
            switch (message.getMode()) {
                case BEGIN:
                    switch (message.getType()) {
                        case KEY -> dataBuilder.beginKey();
                        case LIST -> dataBuilder.beginList();
                        case MAP -> dataBuilder.beginMap();
                        case PUT -> dataBuilder.beginPut();
                        case VALUE -> dataBuilder.beginValue();
                        default -> throw new IllegalStateException();
                    }
                    break;
                case END:
                    switch (message.getType()) {
                        case KEY -> dataBuilder.endKey();
                        case LIST -> dataBuilder.endList(message.getTypeHint());
                        case MAP -> dataBuilder.endMap(message.getTypeHint());
                        case PUT -> dataBuilder.endPut(message.getKeyHint());
                        case VALUE -> dataBuilder.endValue(message.getTypeHint());
                        default -> throw new IllegalStateException();
                    }
                    break;
                default:
                    throw new IllegalStateException();
            }

            message.setPending(false);
        }
    }

    public Object getResult() {
        return null;
    }
}
