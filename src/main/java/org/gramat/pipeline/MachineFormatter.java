package org.gramat.pipeline;

import org.gramat.errors.ErrorFactory;
import org.gramat.machines.Automaton;
import org.gramat.machines.Link;
import org.gramat.machines.LinkEmpty;
import org.gramat.machines.LinkReference;
import org.gramat.machines.LinkSymbol;
import org.gramat.machines.Machine;
import org.gramat.machines.MachineContract;
import org.gramat.machines.MachineProgram;
import org.gramat.machines.Node;

import java.io.IOException;

public class MachineFormatter {

    private boolean ignoreActions;

    public void writeProgram(Appendable output, MachineProgram program) {
        writeComment(output, "main");
        writeMachine(output, program.main);
        for (var entry : program.dependencies.entrySet()) {
            writeNewLine(output);
            writeComment(output, entry.getKey());
            writeMachine(output, entry.getValue());
        }
    }

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
    }

    public void writeMachine(Appendable output, MachineContract machine) {
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
        write(output, generateLabel(link));
        writeNewLine(output);
    }

    private String generateLabel(Link link) {
        var label = new StringBuilder();

        if (link instanceof LinkSymbol) {
            generateLabel((LinkSymbol)link, label);
        }
        else if (link instanceof LinkReference) {
            generateLabel((LinkReference)link, label);
        }
        else if (link instanceof LinkEmpty) {
            label.append("empty");
        }
        else {
            throw ErrorFactory.notImplemented();
        }

        return label.toString()
                .replace("\\", "\\\\")
                .replace(":", "\\:")
                .replace(",", "\\,");
    }

    private void generateLabel(LinkSymbol link, StringBuilder label) {
        if (!ignoreActions) {
            for (var action : link.beginActions) {
                label.append(action.toString());
                label.append(" >> ");
            }
        }

        label.append(link.symbol);

        if (!ignoreActions) {
            if (link.token != null) {
                label.append(" [").append(link.token).append("]");
            }

            for (var action : link.endActions) {
                label.append(" << ");
                label.append(action.toString());
            }
        }
    }

    private void generateLabel(LinkReference link, StringBuilder label) {
        if (!ignoreActions) {
            for (var action : link.beginActions) {
                label.append(action.toString());
                label.append(" >> ");
            }
        }

        label.append(link.name);

        if (!ignoreActions) {
            if (link.token != null) {
                label.append(" [").append(link.token).append("]");
            }

            for (var action : link.endActions) {
                label.append(" << ");
                label.append(action.toString());
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

    private void write(Appendable output, String text) {
        try {
            output.append(text);
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
