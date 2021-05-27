package org.gramat.automata.evaluation;

import org.gramat.automata.State;
import org.gramat.automata.tapes.Tape;

public class Evaluator {

    public Object eval(State initial, Tape tape) {
        var context = new Context();
        var endState = eval(initial, tape, context);

        if (!endState.isAccepted()) {
            throw new RejectedException("Input was not accepted", tape.getLocation(), endState);
        }
        else if (tape.isOpen()) {
            throw new RejectedException("Expected end of content", tape.getLocation(), endState);
        }

        return context.getResult();
    }

    private State eval(State initial, Tape tape, Context context) {
        var state = initial;

        while (tape.isOpen()) {
            var symbol = tape.getChar();
            var transition = state.findTransitionFor(symbol, context.getToken());
            if (transition == null) {
                break;
            }

            context.run(tape, transition.getBeginActions());

            tape.moveForward();

            context.run(tape, transition.getEndActions());

            // Move to next state
            state = transition.getTarget();
        }

        return state;
    }

}
