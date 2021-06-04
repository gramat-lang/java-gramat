package org.gramat.automata.messages;

import org.gramat.automata.State;
import org.gramat.automata.actions.ActionMode;
import org.gramat.automata.actions.ActionType;

public abstract class Message {

    public abstract ActionType getType();

    public abstract ActionMode getMode();

    protected State state;

    protected final int group;

    private boolean pending;

    protected Message(int group) {
        this.group = group;
        this.pending = true;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public int getGroup() {
        return group;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setKeyHint(String typeHint) {
        throw new IllegalStateException();
    }

    public String getKeyHint() {
        throw new IllegalStateException();
    }

    public void setTypeHint(String typeHint) {
        throw new IllegalStateException();
    }

    public String getTypeHint() {
        throw new IllegalStateException();
    }

    public boolean hasPosition() {
        return false;
    }

    public void setPosition(int position) {
        throw new IllegalStateException();
    }

    public int getPosition() {
        throw new IllegalStateException();
    }
}
