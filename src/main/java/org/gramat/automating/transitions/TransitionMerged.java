package org.gramat.automating.transitions;

import org.gramat.actions.ActionList;
import org.gramat.automating.Automaton;
import org.gramat.automating.Direction;
import org.gramat.automating.Level;
import org.gramat.automating.State;
import org.gramat.codes.Code;

public class TransitionMerged extends Transition {

    public final Code code;
    public final ActionList beginActions;
    public final ActionList endActions;

    public TransitionMerged(Automaton am, State source, State target, Code code, ActionList beginActions, ActionList endActions) {
        super(am, source, target);
        this.code = code;
        this.beginActions = beginActions;
        this.endActions = endActions;
    }

    @Override
    public Transition derive(State newSource, State newTarget) {
        return new TransitionMerged(am, newSource, newTarget, code, beginActions.copy(), endActions.copy());
    }
}
