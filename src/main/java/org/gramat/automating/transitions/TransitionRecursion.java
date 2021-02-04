package org.gramat.automating.transitions;

import org.gramat.automating.Automaton;
import org.gramat.automating.Direction;
import org.gramat.automating.Level;
import org.gramat.automating.State;

public class TransitionRecursion extends Transition {
    public final String name;
    public final Level level;
    public final Direction direction;
    public final int pairID;

    public TransitionRecursion(Automaton am, State source, State target, String name, Level level, int pairID, Direction direction) {
        super(am, source, target);
        this.name = name;
        this.level = level;
        this.pairID = pairID;
        this.direction = direction;
    }

    @Override
    public Transition derive(State newSource, State newTarget) {
        return new TransitionRecursion(am, newSource, newTarget, name, level, pairID, direction);
    }
}
