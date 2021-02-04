package org.gramat.automating.engines;

import org.gramat.actions.ActionList;
import org.gramat.automating.Automaton;
import org.gramat.automating.DeterministicMachine;
import org.gramat.automating.Level;
import org.gramat.automating.Machine;
import org.gramat.automating.StateSet;
import org.gramat.logging.Logger;

import java.util.HashSet;
import java.util.LinkedList;

public class DeterministicEngine {

    private final Logger logger;

    public DeterministicEngine(Logger logger) {
        this.logger = logger;
    }

    public DeterministicMachine resolve(Machine nMachine) {
        var dam = new Automaton();
        var dc = new DeterministicContext(dam);
        var nam = nMachine.am;
        var queue = new LinkedList<StateSet>();
        var control = new HashSet<String>();
        var closure0 = nam.emptyClosure(nMachine.begin);

        queue.add(closure0);

        logger.debug("Resolving %s codes and %s levels...", nam.codes.size(), nam.levels.size());

        do {
            var closure = queue.remove();
            if (control.add(closure.getID())) {
                var dSource = dc.mapClosure(closure);

                for (var code : nam.codes) {
//                    for (var level : nam.levels) {
                        var targets = StateSet.of();
                        var beforeActions = new ActionList();
                        var afterActions = new ActionList();

                        for (var t : nam.transitionsFrom(closure, code, Level.ANY)) {
                            beforeActions.prependAll(t.beforeActions);
                            afterActions.appendAll(t.afterActions);

                            targets.add(t.target);
                        }

                        if (targets.isPresent()) {
                            var targetClosure = nam.emptyClosure(targets);
                            var dTarget = dc.mapClosure(targetClosure);

                            dam.addSymbol(dSource, dTarget, code, Level.ANY, beforeActions, afterActions);

                            queue.add(targetClosure);
                        }
//                    }
                }
            }
        }
        while (!queue.isEmpty());

        var begin = dc.unmapClosure(closure0);
        var ends = dc.unmapWith(nMachine.end);
        return new DeterministicMachine(dam, begin, ends);
    }

}
