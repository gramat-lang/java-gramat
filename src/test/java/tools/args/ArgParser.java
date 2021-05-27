package tools.args;

import lombok.extern.slf4j.Slf4j;
import tools.Resources;

import java.util.ArrayList;
import java.util.Iterator;

@Slf4j
public class ArgParser {

    private static final String END_GROUP = "----------";
    private static final String END_GROUP_FOCUSED = "----------^";
    private static final String END_GROUP_IGNORED = "----------#";

    public static ArgSession parse(String... resources) {
        var session = new ArgSession();

        for (var resource : resources) {
            log.debug("Reading arguments from {}...", resource);

            readResource(resource, session);
        }

        boolean onlyFocused = false;

        for (var group : session) {
            if (group.getMode() == ArgMode.FOCUSED) {
                onlyFocused = true;
                break;
            }
        }

        for (var group : session) {
            if (onlyFocused) {
                group.setIgnored(group.getMode() != ArgMode.FOCUSED);
            }
            else {
                group.setIgnored(group.getMode() == ArgMode.IGNORED);
            }
        }

        log.debug("Arguments read: {} group(s)", session.getGroupCount());

        return session;
    }

    private static void readResource(String resource, ArgSession session) {
        var reader = new LineReader(Resources.loadLines(resource));

        while (reader.isOpen()) {
            var group = readGroup(resource, reader);

            session.addGroup(group);
        }
    }

    private static ArgGroup readGroup(String resource, LineReader reader) {
        var group = new ArgGroup(resource);

        while (reader.isOpen()) {
            reader.skipEmptyLines();

            if (group.getLineNumber() == null) {
                group.setLineNumber(reader.getLineNumber());
            }

            if (reader.pull(END_GROUP)) {
                break;
            }
            else if (reader.pull(END_GROUP_FOCUSED)) {
                group.setMode(ArgMode.FOCUSED);
                break;
            }
            else if (reader.pull(END_GROUP_IGNORED)) {
                group.setMode(ArgMode.IGNORED);
                break;
            }
            else {
                var lineNumber = reader.getLineNumber();
                var nameLine = reader.pull();
                var separatorIndex = nameLine.indexOf(':');
                if (separatorIndex == -1) {
                    throw new RuntimeException(resource + ":" + lineNumber + ": expected entry name: " + nameLine);
                }

                var name = nameLine.substring(0, separatorIndex).trim();
                var inlineValue = nameLine.substring(separatorIndex+1).trim();
                if (inlineValue.isEmpty()) {
                    var value = readValueBlock(resource, reader);

                    group.addEntry(new ArgEntry(name, value));
                }
                else {
                    group.addEntry(new ArgEntry(name, inlineValue));
                }
            }
        }

        return group;
    }

    private static String readValueBlock(String resource, LineReader reader) {
        var valueLines = new ArrayList<String>();
        var indentation = computeIndentation(reader.peek());
        if (indentation == null) {
            return "";
        }

        while (reader.isOpen()) {
            var line = reader.peek();

            if (line.startsWith(indentation)) {
                var content = line.substring(indentation.length());

                valueLines.add(content);

                reader.move();
            } else {
                break;
            }
        }

        if (valueLines.isEmpty()) {
            throw new RuntimeException("missing value");
        }

        return String.join("\n", valueLines);
    }

    private static String computeIndentation(String line) {
        var beginIndex = 0;
        while (beginIndex < line.length()) {
            var c = line.charAt(beginIndex);
            if (c == ' ' || c == '\t') {
                beginIndex++;
            } else {
                break;
            }
        }

        if (beginIndex == 0) {
            return null;
        }

        return line.substring(0, beginIndex);
    }

}
