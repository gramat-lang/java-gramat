package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.evaluation.Context;
import org.gramat.automata.tapes.Tape;

@Slf4j
public class PutEnd extends Action {
    public final String nameHint;

    PutEnd(int group, String nameHint) {
        super(group);
        this.nameHint = nameHint;
    }

    @Override
    public void run(Tape tape, Context context) {
        log.debug("RUN {}", this);
    }

    @Override
    public String toString() {
        if (nameHint != null) {
            return String.format("put-end(%s, %s)", group, nameHint);
        }
        return String.format("put-end(%s)", group);
    }
}
