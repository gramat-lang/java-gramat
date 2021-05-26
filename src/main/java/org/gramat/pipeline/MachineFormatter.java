package org.gramat.pipeline;

import org.gramat.actions.Actions;
import org.gramat.errors.ErrorFactory;
import org.gramat.machine.Machine;
import org.gramat.machine.links.Link;
import org.gramat.machine.nodes.Node;
import org.gramat.machine.nodes.NodeNavigator;
import org.gramat.machine.nodes.NodeSet;
import org.gramat.tools.DataUtils;

import java.io.IOException;
import java.util.ArrayList;
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
        var leftPattern = left.hasPattern() ? left.getPattern().toString() : "";
        var rightPattern = right.hasPattern() ? right.getPattern().toString() : "";
        var resultPattern = leftPattern.compareTo(rightPattern);

        if (resultPattern != 0) {
            return resultPattern;
        }

        var leftSourceKey = getKey(left.getSource());
        var rightSourceKey = getKey(right.getSource());
        var resultSource = leftSourceKey.compareTo(rightSourceKey);

        if (resultSource != 0) {
            return resultSource;
        }

        var leftTargetKey = getKey(left.getTarget());
        var rightTargetKey = getKey(right.getTarget());
        var resultTarget = leftTargetKey.compareTo(rightTargetKey);

        if (resultTarget != 0) {
            return resultTarget;
        }

        // TODO compare actions
        return 0;
    }

    public void writeMachine(Appendable output, Machine machine) {
        write(output, NodeSet.of(machine.source()), machine.targets(), machine.links());
    }

    public void write(Appendable output, NodeSet sources, NodeSet targets, Iterable<? extends Link> links) {
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

        if (link.hasPattern()) {
            writeLabel(label, link.getPattern().toString());
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

    private void writeLabel(Appendable output, String label) {
        write(output, label);
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
