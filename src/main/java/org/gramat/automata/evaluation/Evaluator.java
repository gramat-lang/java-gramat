package org.gramat.automata.evaluation;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.State;
import org.gramat.automata.builder.DataBuilder;
import org.gramat.automata.tapes.Tape;
import org.gramat.tools.PP;

@Slf4j
public class Evaluator {

    public Object eval(State initial, Tape tape) {
        return eval(initial, tape, true);
    }

    public Object eval(State initial, Tape tape, boolean consumeAll) {
        var builder = new DataBuilder(tape);
        var state = initial;

        while (tape.isOpen()) {
            var ch = tape.getChar();
            var transition = state.findTransitionFor(ch, builder.getToken());
            if (transition == null) {
                break;
            }

            log.debug("MOVE TO {} @ {}", PP.ch(ch), tape.getLocation());

            for (var action : transition.getBeginActions()) {
                log.debug("RUN BEGIN {}", action);
            }

            builder.run(transition.getBeginActions());

            log.debug("MOVE FORWARD");

            tape.moveForward();

            for (var action : transition.getEndActions()) {
                log.debug("RUN END {}", action);
            }

            builder.run(transition.getEndActions());

            // Move to next state
            state = transition.getTarget();
        }

        if (!state.isAccepted()) {
            throw new RejectedException("Input was not accepted", tape.getLocation(), state);
        }
        else if (consumeAll && tape.isOpen()) {
            throw new RejectedException("Expected end of content", tape.getLocation(), state);
        }

        return builder.build();
    }

}
