package org.gramat.pipeline;

import org.gramat.data.links.Links;
import org.gramat.data.nodes.Nodes;
import org.gramat.errors.ErrorFactory;
import org.gramat.graphs.Automaton;
import org.gramat.graphs.Machine;
import org.gramat.graphs.Node;
import org.gramat.graphs.links.Link;
import org.gramat.graphs.links.LinkEmpty;
import org.gramat.graphs.links.LinkSymbol;

import java.io.IOException;

public class MachineFormatter {

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
        write(output, Nodes.of(automaton.initial), automaton.accepted, automaton.links);
    }

    public void writeMachine(Appendable output, Machine machine) {
        write(output, Nodes.of(machine.source), Nodes.of(machine.target), machine.links);
    }

    public void write(Appendable output, Nodes sources, Nodes targets, Links links) {
        for (var source : sources) {
            writeInitial(output, source);
        }

        for (var link : links) {
            writeLink(output, link);
        }

        for (var target : targets) {
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
            writeLabel(label, linkSym.symbol.toString());
        }
        else if (link instanceof LinkEmpty linkEmp) {
            writeLabel(label, "empty");
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

    private void writeLabel(Appendable output, String symbol) {
        write(output, symbol);
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
}
