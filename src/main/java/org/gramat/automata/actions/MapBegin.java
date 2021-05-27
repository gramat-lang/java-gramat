package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.evaluation.Context;
import org.gramat.automata.tapes.Tape;

@Slf4j
public class MapBegin extends Action {

    MapBegin(int group) {
        super(group);
    }

    @Override
    public void run(Tape tape, Context context) {
        log.debug("RUN {}", this);
    }

    @Override
    public String toString() {
        return String.format("map-begin(%s)", group);
    }
}
