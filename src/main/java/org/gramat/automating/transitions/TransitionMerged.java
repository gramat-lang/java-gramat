package org.gramat.automating.transitions;

import org.gramat.actions.ActionList;
import org.gramat.actions.design.ActionTemplate;
import org.gramat.automating.ActionPlace;
import org.gramat.automating.Automaton;
import org.gramat.automating.Direction;
import org.gramat.automating.Level;
import org.gramat.automating.State;
import org.gramat.codes.Code;

import java.util.ArrayList;
import java.util.List;

public class TransitionMerged extends Transition {

    public final Code code;
    public final List<ActionTemplate> beginActions;
    public final List<ActionTemplate> endActions;

    public TransitionMerged(Automaton am, State source, State target, Code code, List<ActionTemplate> beginActions, List<ActionTemplate> endActions) {
        super(am, source, target);
        this.code = code;
        this.beginActions = beginActions;
        this.endActions = endActions;
    }

    @Override
    public Transition derive(State newSource, State newTarget) {
        return new TransitionMerged(am, newSource, newTarget, code, new ArrayList<>(beginActions), new ArrayList<>(endActions));
    }
}
