package org.gramat.automating.engines;

import org.gramat.automating.Automaton;
import org.gramat.automating.Branch;
import org.gramat.automating.Direction;
import org.gramat.automating.Level;
import org.gramat.automating.State;
import org.gramat.automating.transitions.Transition;
import org.gramat.automating.transitions.TransitionAction;
import org.gramat.automating.transitions.TransitionEmpty;
import org.gramat.automating.transitions.TransitionRecursion;
import org.gramat.automating.transitions.TransitionSymbol;
import org.gramat.exceptions.GramatException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BranchEngine {

    public static List<Branch> branches(Automaton am, Set<State> states, Direction dir) {
        var engine = new BranchEngine(am, dir);

        for (var state : states) {
            engine.runState(state, Level.ANY, 0);
        }

        return engine.branches;
    }

    private final Automaton am;
    private final Direction dir;
    private final List<Branch> branches;
    private final Deque<State> stateStack;
    private final Deque<TransitionAction> actionsStack;
    private final Deque<TransitionRecursion> recursionStack;

    private BranchEngine(Automaton am, Direction dir) {
        this.am = am;
        this.dir = dir;
        this.branches = new ArrayList<>();
        this.stateStack = new ArrayDeque<>();
        this.actionsStack = new ArrayDeque<>();
        this.recursionStack = new ArrayDeque<>();
    }

    private void runState(State base, Level level, int pairID) {
        var queue = new ArrayDeque<State>();
        var control = new HashSet<>();

        queue.add(base);

        while (!queue.isEmpty()) {
            var state = queue.remove();
            if (control.add(state)) {
                for (var t : am.findTransitions(state, dir)) {
                    if (t instanceof TransitionEmpty) {
                        queue.add(t.target);
                    }
                    else if (t instanceof TransitionAction) {
                        queue.add(t.target);
                    }
                    else if (t instanceof TransitionRecursion) {
                        var tr = (TransitionRecursion) t;

                        if (tr.direction == Direction.FORWARD) {
                            runState(tr.target, tr.level, tr.pairID);
                        }
                        else if (tr.direction == Direction.BACKWARD) {
                            // TODO runState but with backward :V
                        }
                        else {
                            throw new GramatException("invalid dir");
                        }
                    }
                    else if (t instanceof TransitionSymbol) {
                        var ts = (TransitionSymbol) t;
                        var branch = new Branch();
                        branch.code = ts.code;
                        branch.level = level;
                        branch.target = ts.target;
                        branches.add(branch);
                    }
                    else {
                        throw new GramatException("invalid transition");
                    }
                }
            }
        }
    }

}
