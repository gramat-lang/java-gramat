package org.gramat.automating.engines;

import org.gramat.actions.ActionList;
import org.gramat.automating.Automaton;
import org.gramat.automating.Closure;
import org.gramat.automating.DeterministicMachine;
import org.gramat.automating.Direction;
import org.gramat.automating.Level;
import org.gramat.automating.Machine;
import org.gramat.automating.State;
import org.gramat.exceptions.GramatException;
import org.gramat.logging.Logger;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class DeterministicEngine {

    public static DeterministicMachine resolve(Machine nMachine, Logger logger) {
        return new DeterministicEngine(logger).resolve(nMachine);
    }

    private final Logger logger;
    private final Map<String, Closure> idClosures;
    private final Map<String, State> idStates;
    private final Automaton dAuto;

    private DeterministicEngine(Logger logger) {
        this.logger = logger;
        this.idClosures = new LinkedHashMap<>();
        this.idStates = new LinkedHashMap<>();
        this.dAuto = new Automaton();
    }

    private DeterministicMachine resolve(Machine nMachine) {
        var nAuto = nMachine.am;
        var queue = new LinkedList<Closure>();
        var control = new HashSet<String>();
        var closure0 = ClosureEngine.empty(nMachine.begin, Direction.FORWARD);

        queue.add(closure0);

        var levels = nAuto.listLevels();
        var codes = nAuto.listCodes();

        logger.debug("Resolving %s codes and %s levels...", codes.size(), levels.size());

        do {
            var closure = queue.remove();
            if (control.add(closure.getID())) {
                var dSource = mapClosure(closure);

                for (var branch : BranchEngine.branches(nAuto, closure.getStates(), Direction.FORWARD)) {
                    var target = ClosureEngine.empty(branch.target, Direction.FORWARD);
                    var dTarget = mapClosure(target);

                    dAuto.addSymbol(dSource, dTarget, branch.code, branch.level, new ActionList(), new ActionList());

                    queue.add(target);
                }
            }
        }
        while (!queue.isEmpty());

        var begin = unmapClosure(closure0);
        var ends = unmapWith(nMachine.end);
        return new DeterministicMachine(dAuto, begin, ends);
    }

    private boolean containsClosureID(String id) {
        return idStates.containsKey(id);
    }

    private State mapClosure(Closure closure) {
        return idStates.computeIfAbsent(closure.getID(), closureID -> {
            var state = dAuto.createState();

            idClosures.put(closureID, closure);

            return state;
        });
    }

    private State unmapClosure(Closure closure) {
        var state = idStates.get(closure.getID());
        if (state == null) {
            throw new GramatException("state not found: " + closure.getID());
        }
        return state;
    }

    private Set<State> unmapWith(State end) {
        var result = new LinkedHashSet<State>();

        for (var closure : idClosures.values()) {
            if (closure.contains(end)) {
                var state = unmapClosure(closure);

                result.add(state);
            }
        }

        return result;
    }
}
