package org.gramat.formatting;

import org.gramat.automating.Automaton;
import org.gramat.automating.DeterministicMachine;
import org.gramat.automating.Level;
import org.gramat.automating.Machine;
import org.gramat.automating.State;
import org.gramat.automating.transitions.Transition;
import org.gramat.automating.transitions.TransitionBackward;
import org.gramat.automating.transitions.TransitionEmpty;
import org.gramat.automating.transitions.TransitionEnter;
import org.gramat.automating.transitions.TransitionExit;
import org.gramat.automating.transitions.TransitionForward;
import org.gramat.automating.transitions.TransitionReference;
import org.gramat.automating.transitions.TransitionSymbol;
import org.gramat.automating.transitions.TransitionWrapper;
import org.gramat.codes.Code;
import org.gramat.codes.CodeChar;
import org.gramat.codes.CodeRange;
import org.gramat.exceptions.GramatException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AutomatonFormatting {

    public static final int NONE = 0;
    public static final int BEFORE = 1;
    public static final int AFTER = 2;

    private final Appendable out;

    public AutomatonFormatting(Appendable out) {
        this.out = out;
    }

    public void write(DeterministicMachine machine) {
        writeRaw("-> ");
        writeStateName(machine.initial);
        writeRaw("\n");

        for (var t : machine.am.transitions) {
            write(t);
        }

        for (var accepted : machine.accepted) {
            writeStateName(accepted);
            writeRaw("<=\n");
        }
    }

    public void write(Machine machine) {
        writeRaw("-> ");
        writeStateName(machine.begin);
        writeRaw("\n");

        for (var t : machine.am.transitions) {
            write(t);
        }

        writeStateName(machine.end);
        writeRaw("<=\n");
    }

    public void write(Transition transition) {
        var symbols = new ArrayList<String>();

        if (transition instanceof TransitionWrapper) {
            var tw = (TransitionWrapper)transition;
            for (var action : tw.beforeActions) {
                symbols.add("BFR " + action);
            }
        }


        if (transition instanceof TransitionForward) {
            var tf = (TransitionForward)transition;

            symbols.add("FRW " + tf.action);
        }
        else if (transition instanceof TransitionBackward) {
            var tb = (TransitionBackward)transition;

            symbols.add("BKW " + tb.action);
        }
        else if (transition instanceof TransitionSymbol) {
            var ts = (TransitionSymbol)transition;

            symbols.add("SYM " + ts.code + "/" + ts.level);
        }
        else if (transition instanceof TransitionEnter) {
            var te = (TransitionEnter)transition;
            symbols.add("ENT " + te.name + "/" + te.level.toString());
        }
        else if (transition instanceof TransitionExit) {
            var te = (TransitionExit)transition;

            symbols.add("EXT " + te.name + "/" + te.level);
        }
        else if (transition instanceof TransitionReference) {
            var tr = (TransitionReference)transition;

            symbols.add("REF " + tr.name + "/" + tr.level);
        }
        else if (!(transition instanceof TransitionEmpty)) {
            throw new GramatException("invalid transition");
        }


        if (transition instanceof TransitionWrapper) {
            var tw = (TransitionWrapper)transition;
            for (var action : tw.afterActions) {
                symbols.add("AFR " + action);
            }
        }

        writeTransition(transition, symbols);
    }

    private void writeTransition(Transition transition, List<String> symbols) {
        writeStateName(transition.source);
        writeRaw(" -> ");
        writeStateName(transition.target);
        if (symbols != null && !symbols.isEmpty()) {
            writeRaw(" : ");
            for (var i = 0; i < symbols.size(); i++) {
                if (i > 0) {
                    writeRaw(", ");
                }
                writeEscaped(symbols.get(i));
            }
        }
        writeRaw("\n");
    }

    private void writeEscaped(String text) {
        writeRaw(text.replace(":", "\\:").replace("!", "\\!").replace("\n", "\\\\n").replace(",", "\\,"));
    }

    private void writeTransition(State source, State target, Code code, Level level, String action, int actionDir) {
        writeStateName(source);
        writeRaw(" -> ");
        writeStateName(target);
        if (code != null) {
            writeRaw(" : ");
            writeCode(code);
            writeRaw("/");
            writeRaw(level.toString());
        }

        if (actionDir != NONE) {
            if (actionDir == BEFORE) {
                writeRaw(" !< ");
            }
            else if (actionDir == AFTER) {
                writeRaw(" !> ");
            }
            else {
                throw new GramatException("invalid action direction");
            }

            if (action != null) {
                writeRaw(action);
            }
        }

        writeRaw("\n");
    }

    private void writeCode(Code code) {
        if (code instanceof CodeChar) {
            var c = (CodeChar)code;

            writeChar(c.value);
        }
        else if (code instanceof CodeRange) {
            var r = (CodeRange)code;

            writeChar(r.begin);
            writeRaw("-");
            writeChar(r.end);
        }
    }

    private void writeChar(char c) {
        writeRaw("'");
        switch (c) {
            case '\n':
                writeRaw("\\\\n");
                break;
            case '\r':
                writeRaw("\\\\r");
                break;
            case '\t':
                writeRaw("\\\\t");
                break;
            case '\\':
            case ':':
                writeRaw("\\");
                writeRaw(c);
                break;
            default:
                writeRaw(c);
                break;
        }
        writeRaw("'");
    }

    private void writeStateName(State state) {
        if (state.wild) {
            writeRaw("W");
        }
        else {
            writeRaw("S");
        }
        writeRaw(String.valueOf(state.id));
    }

    private void writeRaw(char c) {
        try {
            out.append(c);
        }
        catch (IOException e) {
            throw new GramatException("output error", e);
        }
    }

    private void writeRaw(String text) {
        try {
            out.append(text);
        }
        catch (IOException e) {
            throw new GramatException("output error", e);
        }
    }

}
