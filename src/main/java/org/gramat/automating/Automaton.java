package org.gramat.automating;

import org.gramat.actions.Action;
import org.gramat.actions.ActionList;
import org.gramat.automating.transitions.Transition;
import org.gramat.automating.transitions.TransitionAction;
import org.gramat.automating.transitions.TransitionEmpty;
import org.gramat.automating.transitions.TransitionMerged;
import org.gramat.automating.transitions.TransitionRecursion;
import org.gramat.automating.transitions.TransitionReference;
import org.gramat.automating.transitions.TransitionSymbol;
import org.gramat.codes.Code;
import org.gramat.codes.CodeChar;
import org.gramat.codes.CodeRange;
import org.gramat.exceptions.GramatException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Automaton {
    public final List<State> states;
    public final List<Transition> transitions;
    public final List<Code> codes;
    public final List<Level> levels;

    public final MachineMap machines;

    public final Level any;

    private int currentStateID;
    private int currentLevelID;

    public Automaton() {
        this.states = new ArrayList<>();
        this.transitions = new ArrayList<>();
        this.codes = new ArrayList<>();
        this.levels = new ArrayList<>();
        this.machines = new MachineMap();

        this.any = createLevel();
    }

    public Level createLevel() {
        var level = new Level(currentLevelID);

        currentLevelID++;

        levels.add(level);

        return level;
    }


    public Code getChar(char value) {
        for (var code : codes) {
            if (code instanceof CodeChar && ((CodeChar) code).value == value) {
                return code;
            }
        }

        var code = new CodeChar(value);

        codes.add(code);

        return code;
    }

    public Code getRange(char begin, char end) {
        for (var code : codes) {
            if (code instanceof CodeRange) {
                var range = (CodeRange)code;

                // TODO validate overlaps between ranges

                if (range.begin == begin && range.end == end) {
                    return code;
                }
            }
        }

        var code = new CodeRange(begin, end);

        codes.add(code);

        return code;
    }

    public State createState(boolean wild) {
        currentStateID++;
        var s = new State(this, currentStateID, wild);
        states.add(s);
        return s;
    }

    public State createState() {
        return createState(false);
    }

    public State createWild() {
        return createState(true);
    }

    public Machine createMachine() {
        return new Machine(this, createState(), createState());
    }

    public Machine createMachine(State begin, State end) {
        return new Machine(this, begin, end);
    }

    public TransitionEmpty addEmpty(State source, State target) {
        var t = new TransitionEmpty(this, source, target);
        transitions.add(t);
        return t;
    }

    public TransitionSymbol addSymbol(State source, State target, Code code) {
        var t = new TransitionSymbol(this, source, target, code);
        transitions.add(t);
        return t;
    }

    public TransitionMerged addMerged(State source, State target, Code code, ActionList beginActions, ActionList endActions) {
        var t = new TransitionMerged(this, source, target, code, beginActions, endActions);
        transitions.add(t);
        return t;
    }

    public TransitionAction addAction(State source, State target, Action action, Direction direction) {
        var t = new TransitionAction(this, source, target, action, direction);
        transitions.add(t);
        return t;
    }

    public TransitionRecursion addRecursion(State source, State target, String name, Level level, int pairID, Direction direction) {
        var t = new TransitionRecursion(this, source, target, name, level, pairID, direction);
        transitions.add(t);
        return t;
    }

    public TransitionReference addReference(State source, State target, String name, Level level) {
        var t = new TransitionReference(this, source, target, name, level);
        transitions.add(t);
        return t;
    }

    public void removeState(State state) {
        for (var t : transitions) {
            if (t.source == state || t.target == state) {
                throw new GramatException("cannot remove state: " + state);
            }
        }

        states.remove(state);
    }

    public void removeTransition(Transition transition) {
        if (!transitions.remove(transition)) {
            throw new GramatException("cannot remove transition");
        }
    }

    public void walkForward(State initial, Predicate<Transition> control) {
        var stack = new ArrayDeque<State>();

        stack.push(initial);

        do {
            var state = stack.pop();

            for (var t : transitionsFrom(state)) {
                if (control.test(t)) {
                    stack.push(t.target);
                }
            }
        } while (!stack.isEmpty());
    }

    public void walkBackward(State initial, Predicate<Transition> control) {
        var stack = new ArrayDeque<State>();

        stack.push(initial);

        do {
            var state = stack.pop();

            for (var t : transitionsTo(state)) {
                if (control.test(t)) {
                    stack.push(t.source);
                }
            }
        } while (!stack.isEmpty());
    }

    public List<Transition> transitionsFrom(State state) {
        return transitions.stream()
                .filter(t -> t.source == state)
                .collect(Collectors.toList());
    }

    public List<Transition> transitionsTo(State state) {
        return transitions.stream()
                .filter(t -> t.target == state)
                .collect(Collectors.toList());
    }

    public StateSet emptyClosure(State state) {
        return emptyClosure(StateSet.of(state));
    }

    public StateSet emptyClosure(StateSet closure) {
        var states = new HashSet<State>();

        for (var state : closure) {
            states.add(state);

            walkForward(state, t -> {
                if (t instanceof TransitionSymbol) {
                    return false;
                }
                else {
                    states.add(t.target);
                    return true;
                }
            });
        }

        return StateSet.of(states);
    }

    public List<TransitionSymbol> transitionsFrom(StateSet closure, Code code, Level level) {
        var result = new ArrayList<TransitionSymbol>();

        for (var state : closure) {
            for (var t : transitionsFrom(state)) {
                if (t instanceof TransitionSymbol) {
                    var ts = (TransitionSymbol)t;

                    if (ts.code == code) {
                        result.add(ts);
                    }
                }
            }
        }

        return result;
    }

    public <T extends Transition> List<T> listOf(Class<T> type) {
        return transitions.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toList());
    }

    public void deriveTransition(Transition transition, State newSource, State newTarget) {
        var newTransition = transition.derive(newSource, newTarget);

        transitions.add(newTransition);
    }

    public Set<Code> listCodes() {
        var codes = new LinkedHashSet<Code>();
        for (var t : transitions) {
            if (t instanceof TransitionSymbol) {
                codes.add(((TransitionSymbol) t).code);
            }
        }
        return codes;
    }

    public Set<Level> listLevels() {
        var levels = new LinkedHashSet<Level>();
        for (var t : transitions) {
            if (t instanceof TransitionRecursion) {
                levels.add(((TransitionRecursion) t).level);
            }
        }
        return levels;
    }

    public List<Transition> findTransitions(State state, Direction dir) {
        if (dir == Direction.FORWARD) {
            return transitions.stream()
                    .filter(t -> t.source == state)
                    .collect(Collectors.toList());
        }
        else if (dir == Direction.BACKWARD) {
            return transitions.stream()
                    .filter(t -> t.target == state)
                    .collect(Collectors.toList());
        }
        else {
            throw new GramatException("invalid dir");
        }
    }
}
