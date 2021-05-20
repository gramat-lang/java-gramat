package org.gramat.pipeline;

import org.gramat.data.actions.Actions;
import org.gramat.errors.ErrorFactory;
import org.gramat.graphs.Automaton;
import org.gramat.graphs.MachineAction;
import org.gramat.graphs.links.Link;
import org.gramat.graphs.links.LinkEmpty;
import org.gramat.graphs.links.LinkEnter;
import org.gramat.graphs.links.LinkExit;
import org.gramat.graphs.links.LinkSymbol;
import org.gramat.graphs.Machine;
import org.gramat.graphs.Node;

import java.io.IOException;

public class MachineFormatter {

    private boolean ignoreActions;

    public String writeMachine(Machine machine) {
        var builder = new StringBuilder();

        writeMachine(builder, machine);

        return builder.toString();
    }

    public String writeAutomaton(Automaton automaton) {
        var builder = new StringBuilder();

        writeAutomaton(builder, automaton);

        return builder.toString();
    }

    public void writeAutomaton(Appendable output, Automaton automaton) {
        writeInitial(output, automaton.initial);

        for (var link : automaton.links) {
            writeLink(output, link);
        }

        for (var target : automaton.accepted) {
            writeAccepted(output, target);
        }
    }

    public void writeMachine(Appendable output, Machine machine) {
        writeInitial(output, machine.source);

        for (var link : machine.links) {
            writeLink(output, link);
        }

        writeAccepted(output, machine.target);

        for (var action : machine.actions) {
            writeMachine(output, action);
        }
    }

    public void writeMachine(Appendable output, MachineAction machine) {
        writeComment(output, "ACTION " + machine.type + " " + machine.argument);

        for (var source : machine.sources) {
            writeInitial(output, source);
        }

        for (var link : machine.links) {
            writeLink(output, link);
        }

        for (var target : machine.targets) {
            writeAccepted(output, target);
        }
    }

    private void writeAccepted(Appendable output, Node node) {
        writeName(output, node);
        write(output, "<=");
        writeNewLine(output);
    }

    private void writeInitial(Appendable output, Node node) {
        write(output, "->");
        writeName(output, node);
        writeNewLine(output);
    }

    private void writeLink(Appendable output, Link link) {
        writeName(output, link.source);
        write(output, "->");
        writeName(output, link.target);
        write(output, " : ");
        write(output, writeLabel(link));
        writeNewLine(output);
    }

    private String writeLabel(Link link) {
        var label = new StringBuilder();

        if (link instanceof LinkSymbol linkSym) {
            writeLabel(label, linkSym.beginActions, linkSym.symbol.toString(), linkSym.endActions);
        }
        else if (link instanceof LinkEmpty linkEmp) {
            writeLabel(label, linkEmp.beginActions, "empty", linkEmp.endActions);
        }
        else if (link instanceof LinkEnter linkEnt) {
            writeLabel(label, linkEnt.beginActions, "enter(" + linkEnt.token + ")", linkEnt.endActions);
        }
        else if (link instanceof LinkExit linkExi) {
            writeLabel(label, linkExi.beginActions, "exit(" + linkExi.token + ")", linkExi.endActions);
        }
        else {
            throw ErrorFactory.notImplemented();
        }

        return label.toString()
                .replace("\\", "\\\\")
                .replace("\n", "\\\n")
                .replace("!", "\\!")
                .replace(",", "\\,");
    }

    private void writeLabel(Appendable output, Actions beginActions, String symbol, Actions endActions) {
        if (!ignoreActions) {
            for (var action : beginActions) {
                write(output, action);
                write(output, "\n");
            }
        }

        write(output, symbol);

        if (!ignoreActions) {
            for (var action : endActions) {
                write(output, "\n");
                write(output, action);
            }
        }
    }

    private void writeName(Appendable output, Node node) {
        write(output, String.valueOf(node.id));
    }

    private void writeComment(Appendable output, String message) {
        write(output, "# ");
        write(output, message);
        writeNewLine(output);
    }

    private void writeNewLine(Appendable output) {
        write(output, "\n");
    }

    private void write(Appendable output, Object value) {
        try {
            output.append(value.toString());
        }
        catch (IOException e) {
            throw ErrorFactory.internalError(e);
        }
    }

    public boolean isIgnoreActions() {
        return ignoreActions;
    }

    public void setIgnoreActions(boolean ignoreActions) {
        this.ignoreActions = ignoreActions;
    }
}
