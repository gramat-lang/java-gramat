package org.gramat.formatting;

import org.gramat.automating.DeterministicMachine;
import org.gramat.automating.Direction;
import org.gramat.automating.Machine;
import org.gramat.automating.State;
import org.gramat.automating.transitions.Transition;
import org.gramat.automating.transitions.TransitionAction;
import org.gramat.automating.transitions.TransitionEmpty;
import org.gramat.automating.transitions.TransitionMerged;
import org.gramat.automating.transitions.TransitionRecursion;
import org.gramat.automating.transitions.TransitionReference;
import org.gramat.automating.transitions.TransitionSymbol;
import org.gramat.exceptions.GramatException;

import java.util.ArrayList;

public class AutomatonFormatting extends AmFormatter {

    public AutomatonFormatting(Appendable out) {
        super(out);
    }

    public void write(DeterministicMachine machine) {
        writeInitial(getStateName(machine.initial));

        for (var t : machine.am.transitions) {
            writeTransition(t);
        }

        for (var accepted : machine.accepted) {
            writeAccepted(getStateName(accepted));
        }
    }

    public void write(Machine machine) {
        writeInitial(getStateName(machine.begin));

        for (var t : machine.am.transitions) {
            writeTransition(t);
        }

        writeAccepted(getStateName(machine.end));
    }

    public void writeTransition(Transition transition) {
        var symbols = new ArrayList<String>();

        if (transition instanceof TransitionAction) {
            var ta = (TransitionAction)transition;

            if (ta.direction == Direction.FORWARD) {
                symbols.add("FRW " + ta.action);
            }
            else if (ta.direction == Direction.BACKWARD) {
                symbols.add("BKW " + ta.action);
            }
            else {
                throw new GramatException("invalid dir");
            }
        }
        else if (transition instanceof TransitionSymbol) {
            var ts = (TransitionSymbol)transition;

            symbols.add("SYM " + ts.code);
        }
        else if (transition instanceof TransitionMerged) {
            var tm = (TransitionMerged)transition;

            for (var action : tm.beginActions) {
                symbols.add("BGN " + action);
            }

            symbols.add("SYM " + tm.code);

            for (var action : tm.endActions) {
                symbols.add("END " + action);
            }
        }
        else if (transition instanceof TransitionRecursion) {
            var tr = (TransitionRecursion)transition;
            if (tr.direction == Direction.FORWARD) {
                symbols.add("ENT " + tr.name + "/" + tr.level.toString());
            }
            else if (tr.direction == Direction.BACKWARD) {
                symbols.add("EXT " + tr.name + "/" + tr.level.toString());
            }
            else {
                throw new GramatException("invalid dir");
            }
        }
        else if (transition instanceof TransitionReference) {
            var tr = (TransitionReference)transition;

            symbols.add("REF " + tr.name + "/" + tr.level);
        }
        else if (!(transition instanceof TransitionEmpty)) {
            throw new GramatException("invalid transition");
        }

        writeTransition(getStateName(transition.source), getStateName(transition.target), symbols);
    }

    private String getStateName(State state) {
        if (state.wild) {
            return "W" + state.id;
        }
        return "S" + state.id;
    }

}
