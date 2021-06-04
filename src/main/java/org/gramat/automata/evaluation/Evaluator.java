package org.gramat.automata.evaluation;

import org.gramat.automata.State;
import org.gramat.automata.tapes.Tape;

public class Evaluator {

    public Object eval(State initial, Tape tape) {
        return eval(initial, tape, true);
    }

    public Object eval(State initial, Tape tape, boolean consumeAll) {
        var context = new Context(tape);
        var state = initial;

        while (tape.isOpen()) {
            var symbol = tape.getChar();
            var transition = state.findTransitionFor(symbol, context.getToken());
            if (transition == null) {
                break;
            }

            context.run(transition.getBeginActions());

            tape.moveForward();

            context.run(transition.getEndActions());

            // Move to next state
            state = transition.getTarget();
        }

        if (!state.isAccepted()) {
            throw new RejectedException("Input was not accepted", tape.getLocation(), state);
        }
        else if (consumeAll && tape.isOpen()) {
            throw new RejectedException("Expected end of content", tape.getLocation(), state);
        }

        return context.getResult();
    }

}
