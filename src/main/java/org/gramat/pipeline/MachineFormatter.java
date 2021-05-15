package org.gramat.pipeline;

import org.gramat.errors.ErrorFactory;
import org.gramat.machines.Link;
import org.gramat.machines.LinkSymbol;
import org.gramat.machines.Machine;
import org.gramat.machines.MachineContract;
import org.gramat.machines.MachineProgram;
import org.gramat.machines.Node;

import javax.crypto.Mac;
import java.io.IOException;

public class MachineFormatter {

    public boolean ignoreActions;

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

        var label = generateLabel(link);

        write(output, " : ");

        if (label.isBlank()) {
            write(output, "empty");
        }
        else {
            write(output, label);
        }

        writeNewLine(output);
    }

    private String generateLabel(Link link) {
        var label = new StringBuilder();

        if (link instanceof LinkSymbol) {
            generateLabel((LinkSymbol)link, label);
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

        if (link.token != null) {
            label.append("[").append(link.token).append("]");
        }

        if (!ignoreActions) {
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

}
