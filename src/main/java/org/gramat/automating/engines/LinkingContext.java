package org.gramat.automating.engines;

import org.gramat.actions.Action;
import org.gramat.automating.Level;
import org.gramat.automating.State;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class LinkingContext {
    public final Deque<Action> actionStack;
    public final Deque<Level> levelStack;
    public final Deque<State> stateQueue;

    public LinkingContext() {
        this.actionStack = new ArrayDeque<>();
        this.levelStack = new ArrayDeque<>();
        this.stateQueue = new ArrayDeque<>();
    }
}
