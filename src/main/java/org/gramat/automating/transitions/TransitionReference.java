package org.gramat.automating.transitions;

import org.gramat.automating.Automaton;
import org.gramat.automating.Level;
import org.gramat.automating.State;

public class TransitionReference extends Transition {

    public final String name;
    public final Level level;
    public final int reservedEnterID;
    public final int reservedExitID;

    public TransitionReference(Automaton am, State source, State target, String name, Level level, int reservedEnterID, int reservedExitID) {
        super(am, source, target);
        this.name = name;
        this.level = level;
        this.reservedEnterID = reservedEnterID;
        this.reservedExitID = reservedExitID;
    }

    @Override
    public Transition derive(State newSource, State newTarget) {
        return new TransitionReference(am, newSource, newTarget, name, level, reservedEnterID, reservedExitID);
    }
}
