package org.gramat.automata.actions;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.evaluation.Context;
import org.gramat.automata.tapes.Tape;

@Slf4j
public class MapEnd extends Action {
    public final String typeHint;

    MapEnd(int group, String typeHint) {
        super(group);
        this.typeHint = typeHint;
    }

    @Override
    public void run(Tape tape, Context context) {
        log.debug("RUN {}", this);
    }

    @Override
    public String toString() {
        if (typeHint != null) {
            return String.format("map-end(%s, %s)", group, typeHint);
        }
        return String.format("map-end(%s)", group);
    }
}
