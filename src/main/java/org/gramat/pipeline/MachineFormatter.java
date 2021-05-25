package org.gramat.pipeline;

import org.gramat.data.actions.Actions;
import org.gramat.data.nodes.NodeNavigator;
import org.gramat.data.nodes.Nodes;
import org.gramat.errors.ErrorFactory;
import org.gramat.graphs.Automaton;
import org.gramat.graphs.CleanMachine;
import org.gramat.graphs.Machine;
import org.gramat.graphs.Node;
import org.gramat.graphs.links.Link;
import org.gramat.tools.DataUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MachineFormatter {

    private final Map<Node, String> nodeKeys;

    public MachineFormatter() {
        nodeKeys = new HashMap<>();
    }

    public String getKey(Node node) {
        return nodeKeys.computeIfAbsent(node, k -> String.valueOf(nodeKeys.size() + 1));
    }

    public List<Node> sortNodes(Iterable<Node> nodes) {
        var result = new ArrayList<Node>();

        DataUtils.addAll(result, nodes);

        result.sort(Comparator.comparing(this::getKey));

        return result;
    }

    public List<Link> sortLinks(Iterable<? extends Link> links) {
        var result = new ArrayList<Link>();

        DataUtils.addAll(result, links);

        result.sort(this::compareLinks);

        return result;
    }

    private int compareLinks(Link left, Link right) {
        var leftSymbol = left.hasSymbol() ? left.getSymbol().toString() : "";
        var rightSymbol = right.hasSymbol() ? right.getSymbol().toString() : "";
        var symbolResult = leftSymbol.compareTo(rightSymbol);

        if (symbolResult != 0) {
            return symbolResult;
        }

        var leftSourceKey = getKey(left.getSource());
        var rightSourceKey = getKey(right.getSource());
        var sourceResult = leftSourceKey.compareTo(rightSourceKey);

        if (sourceResult != 0) {
            return sourceResult;
        }

        var leftTargetKey = getKey(left.getTarget());
        var rightTargetKey = getKey(right.getTarget());
        var targetResult = leftTargetKey.compareTo(rightTargetKey);

        if (targetResult != 0) {
            return targetResult;
        }

        // TODO compare actions
        return 0;
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
        write(output, Nodes.of(automaton.initial), automaton.accepted, automaton.links);
    }

    public void writeMachine(Appendable output, Machine machine) {
        write(output, Nodes.of(machine.source), Nodes.of(machine.target), machine.links);
    }

    public void writeMachine(Appendable output, CleanMachine machine) {
        write(output, Nodes.of(machine.source()), machine.targets(), machine.links());
    }

    public void write(Appendable output, Nodes sources, Nodes targets, Iterable<? extends Link> links) {
        var printedLinks = new HashSet<Link>();
        var nav = new NodeNavigator();

        for (var source : sortNodes(sources)) {
            nav.push(source);

            writeInitial(output, source);
        }

        while (nav.hasNodes()) {
            var node = nav.pop();

            for (var link : findLinksFrom(node, links)) {
                writeLink(output, link);

                nav.push(link.getTarget());
                printedLinks.add(link);
            }
        }

        for (var target : sortNodes(targets)) {
            writeAccepted(output, target);
        }

        // Unrelated links

        for (var link : links) {
            if (printedLinks.add(link)) {
                writeLink(output, link);
            }
        }
    }

    private List<Link> findLinksFrom(Node source, Iterable<? extends Link> links) {
        var result = new ArrayList<Link>();

        for (var link : links) {
            if (link.getSource() == source) {
                result.add(link);
            }
        }

        return sortLinks(result);
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
        writeName(output, link.getSource());
        write(output, "->");
        writeName(output, link.getTarget());
        write(output, " : ");
        write(output, writeLabel(link));
        writeNewLine(output);
    }

    private String writeLabel(Link link) {
        var label = new StringBuilder();

        writeActions(label, link.getBeforeActions(), "", "\n");

        if (link.hasSymbol()) {
            writeLabel(label, link.getSymbol().toString());
        }
        else if (link.isEmpty()) {
            writeLabel(label, "empty");
        }
        else {
            throw ErrorFactory.notImplemented();
        }

        writeActions(label, link.getAfterActions(), "\n", "");

        return label.toString()
                .replace("\\", "\\\\")
                .replace("\n", "\\\n")
                .replace("!", "\\!")
                .replace(",", "\\,");
    }

    private void writeActions(Appendable output, Actions actions, String prepend, String append) {
        if (actions.isPresent()) {
            write(output, prepend);
            var index = 0;
            for (var action : actions) {
                if (index > 0) {
                    write(output, "\n");
                }
                write(output, action.toString());
                index++;
            }
            write(output, append);
        }
    }

    private void writeLabel(Appendable output, String symbol) {
        write(output, symbol);
    }

    private void writeName(Appendable output, Node node) {
        write(output, getKey(node));
    }

    private void writeComment(Appendable output, String message) {
        write(output, "# ");
        write(output, message);
        writeNewLine(output);
    }

    private void writeNewLine(Appendable output) {
        write(output, "\n");
    }

    public void write(Appendable output, Object value) {
        try {
            output.append(value.toString());
        }
        catch (IOException e) {
            throw ErrorFactory.internalError(e);
        }
    }
}
