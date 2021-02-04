package org.gramat.automating.engines;

import org.gramat.automating.Automaton;
import org.gramat.automating.Machine;
import org.gramat.automating.State;
import org.gramat.automating.transitions.Transition;
import org.gramat.automating.transitions.TransitionEmpty;
import org.gramat.logging.Logger;

import java.util.ArrayList;

public class EmptyResolverEngine {

    private final Logger logger;

    public EmptyResolverEngine(Logger logger) {
        this.logger = logger;
    }

    public Machine resolve(Machine machine) {
        var newBegin = machine.begin;
        var newEnd = machine.end;
        var am = machine.am;

        while (true) {
            var t = findFirstEmptyTransition(am);
            if (t == null) {
                break;
            }

            if (t.source == t.target) {
                am.removeTransition(t);

                logger.debug("Removed empty transition to itself %s.", t.source.id);
            }
            else {
                tryRemoveState(am, t.source);
                tryRemoveState(am, t.target);
            }
        }

        return am.createMachine(newBegin, newEnd);
    }

    private void tryRemoveState(Automaton am, State state) {
        var enteringList = am.transitionsTo(state);
        if (enteringList.size() == 1) {
            var entering = enteringList.get(0);
            var exitingList = am.transitionsFrom(state);
            if (exitingList.size() == 1) {
                var exiting = exitingList.get(0);
                if (entering instanceof TransitionEmpty && exiting instanceof TransitionEmpty) {
                    am.removeTransition(entering);
                    am.removeTransition(exiting);
                    am.removeState(state);

                    var newState = am.createState();

                    replaceState(am, entering.source, newState);
                    replaceState(am, exiting.target, newState);
                }
                else if (entering instanceof TransitionEmpty) {
                    am.removeTransition(entering);

                    replaceState(am, entering.source, state);
                }
                else if (exiting instanceof TransitionEmpty) {
                    am.removeTransition(exiting);

                    replaceState(am, exiting.target, state);
                }
            }
        }
    }

    private void replaceState(Automaton am, State oldState, State newState) {
        while (true) {
            var tST = findFirstTransitionWithSourceAndTarget(am, oldState);
            if (tST != null) {
                am.deriveTransition(tST, newState, newState);
                am.removeTransition(tST);

                logger.debug("Transition %s -> %s was replaced by %s -> %s", tST.source.id, tST.target.id, newState.id, newState.id);
            }

            var tS = findFirstTransitionWithSource(am, oldState);
            if (tS != null) {
                am.deriveTransition(tS, newState, tS.target);
                am.removeTransition(tS);

                logger.debug("Transition %s -> %s was replaced by %s -> %s", tS.source.id, tS.target.id, newState.id, tS.target.id);
            }

            var tT = findFirstTransitionWithTarget(am, oldState);
            if (tT != null) {
                am.deriveTransition(tT, tT.source, newState);
                am.removeTransition(tT);

                logger.debug("Transition %s -> %s was replaced by %s -> %s", tT.source.id, tT.target.id, tT.source.id, newState.id);
            }

            if (tST == null && tS == null && tT == null) {
                break;
            }
        }

        am.removeState(oldState);

        logger.debug("State %s was replaced by %s.", oldState.id, newState.id);
    }

    private Transition findFirstTransitionWithSource(Automaton am, State state) {
        for (var t : am.transitions) {
            if (t.source == state) {
                return t;
            }
        }
        return null;
    }

    private Transition findFirstTransitionWithTarget(Automaton am, State state) {
        for (var t : am.transitions) {
            if (t.target == state) {
                return t;
            }
        }
        return null;
    }

    private Transition findFirstTransitionWithSourceAndTarget(Automaton am, State state) {
        for (var t : am.transitions) {
            if (t.source == state && t.target == state) {
                return t;
            }
        }
        return null;
    }

    private TransitionEmpty findFirstEmptyTransition(Automaton am) {
        for (var t : am.transitions) {
            if (t instanceof TransitionEmpty) {
                return (TransitionEmpty) t;
            }
        }
        return null;
    }

}
