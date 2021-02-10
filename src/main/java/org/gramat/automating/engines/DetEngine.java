package org.gramat.automating.engines;

import org.gramat.automating.Automaton;
import org.gramat.automating.DeterministicMachine;
import org.gramat.automating.Machine;
import org.gramat.logging.Logger;

public interface DetEngine {

    static DeterministicMachine run(Machine nMachine, Logger logger) {
        var am = new Automaton();
        return null;
    }

}
