package org.gramat.automating.engines;

import org.gramat.actions.HeapPop;
import org.gramat.actions.HeapPush;
import org.gramat.automating.ActionPlace;
import org.gramat.automating.Automaton;
import org.gramat.automating.Direction;
import org.gramat.automating.Level;
import org.gramat.automating.Machine;
import org.gramat.automating.transitions.TransitionReference;
import org.gramat.logging.Logger;
import org.gramat.util.BiMap;

import java.util.ArrayList;

public class LinkingEngine {

    public static Machine run(Machine main, Logger logger) {
        var engine = new LinkingEngine(logger);

        return engine.resolve(main);
    }

    private final Logger logger;
    private final BiMap<String, Level, Machine> refLevelMachines;
    private final Automaton am;

    private LinkingEngine(Logger logger) {
        this.logger = logger;
        this.refLevelMachines = new BiMap<>();
        this.am = new Automaton();
    }

    private Machine resolve(Machine main) {
        var mainCopy = new CopyManager(am).copyMachine(main);
        var promises = new ArrayList<Runnable>();

        while(true) {
            var references = am.listOf(TransitionReference.class);

            if (references.isEmpty()) {
                break;
            }

            for (var reference : references) {
                resolveReference(reference, main.am, promises);
            }
        }

        for (var promise : promises) {
            promise.run();
        }

        // TODO improve code & level copying
        am.codes.addAll(main.am.codes);
        am.levels.addAll(main.am.levels);
        return mainCopy;
    }

    private void resolveReference(TransitionReference reference, Automaton am0, ArrayList<Runnable> promises) {
        logger.debug("Resolving reference %s/%s...", reference.name, reference.level);

        var machine = refLevelMachines.get(reference.name, reference.level);

        if (machine == null) {
            // create empty machine...
            machine = am.createMachine();

            // publish it in the map
            refLevelMachines.put(reference.name, reference.level, machine);

            // copy original and connect
            logger.debug("Copy %s %s...", reference.name, reference.level);
            var machine0 = am0.machines.find(reference.name);
            var copyMachine = new CopyManager(am).copyMachine(machine0);

            am.addEmpty(machine.begin, copyMachine.begin);
            am.addEmpty(copyMachine.end, machine.end);
        }

        am.addRecursion(reference.source, machine.begin, reference.name, reference.level, reference.enterAction, Direction.FORWARD);
        am.addRecursion(machine.end, reference.target, reference.name, reference.level, reference.exitAction, Direction.BACKWARD);

        am.removeTransition(reference);
    }

}
