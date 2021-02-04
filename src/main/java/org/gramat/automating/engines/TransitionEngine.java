//package org.gramat.automating.engines;
//
//import org.gramat.actions.Action;
//import org.gramat.automating.Machine;
//import org.gramat.automating.State;
//import org.gramat.automating.transitions.TransitionWrapper;
//import org.gramat.exceptions.GramatException;
//import org.gramat.logging.Logger;
//
//import java.util.concurrent.atomic.AtomicInteger;
//
//public class TransitionEngine {
//
//    private final Logger logger;
//    private final CopyManager copy;
//
//    public TransitionEngine(Logger logger) {
//        this.logger = logger;
//        this.copy = new CopyManager();
//    }
//
//    public Machine resolve(Machine main) {
//        var mainCopy = copy.copyMachine(main);
//
//        resolveActions(mainCopy);
//
//        // TODO improve code & level copying
//        copy.am.codes.addAll(main.am.codes);
//        copy.am.levels.addAll(main.am.levels);
//        return mainCopy;
//    }
//
//    private void resolveActions(Machine machine) {
//        for (var t : machine.findTransitions()) {
//            if (t instanceof TransitionForward) {
//                resolveForward((TransitionForward) t);
//            }
//            else if (t instanceof TransitionBackward) {
//                resolveBackward((TransitionBackward) t);
//            }
//        }
//    }
//
//    private void resolveForward(TransitionForward forward) {
//        resolveForward(forward.target, forward.action);
//
//        copy.am.removeTransition(forward);
//        copy.am.addEmpty(forward.source, forward.target);
//    }
//
//    private void resolveForward(State state, Action action) {
//        var count = new AtomicInteger(0);
//        copy.am.walkForward(state, t -> {
//            if (t instanceof TransitionWrapper) {
//                var tw = (TransitionWrapper)t;
//
//                tw.beforeActions.prepend(action);
//                count.set(count.get() + 1);
//                return false;
//            }
//            return true;
//        });
//        if (count.get() == 0) {
//            throw new GramatException("could not resolve: " + action);
//        }
//        logger.debug("Resolved forward %s x %s", action, count.get());
//    }
//
//    private void resolveBackward(TransitionBackward backward) {
//        resolveBackward(backward.source, backward.action);
//
//        copy.am.removeTransition(backward);
//        copy.am.addEmpty(backward.source, backward.target);
//    }
//
//    private void resolveBackward(State state, Action action) {
//        var count = new AtomicInteger(0);
//        copy.am.walkBackward(state, t -> {
//            if (t instanceof TransitionWrapper) {
//                var tw = (TransitionWrapper) t;
//
//                tw.afterActions.append(action);
//                count.set(count.get() + 1);
//                return false;
//            }
//            return true;
//        });
//        if (count.get() == 0) {
//            throw new GramatException("could not resolve: " + action);
//        }
//        logger.debug("Resolved backward %s x %s", action, count.get());
//    }
//
//}
