package tools;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.provider.Arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ArgumentsParser {

    private static final String END_HEAD = "==========";
    private static final String END_ITEM = "----------";
    private static final String END_FILE = "**********";

    public static List<Arguments> parse(String... resources) {
        var arguments = new ArrayList<Arguments>();

        for (var resource : resources) {
            log.debug("Reading arguments from {}...", resource);

            readResource(resource, arguments);
        }

        log.debug("Reading arguments completed: {} set(s)", arguments.size());

        return arguments;
    }

    private static void readResource(String resource, List<Arguments> arguments) {
        var lines = Arrays.stream(Resources.loadLines(resource)).iterator();
        var fieldNames = new ArrayList<String>();
        while (lines.hasNext()) {
            var line = lines.next();

            if (line.equals(END_HEAD)) {
                break;
            }
            else {
                fieldNames.add(line);
            }
        }

        var valueLines = new ArrayList<String>();
        var fieldValues = new ArrayList<String>();

        boolean flushValue;
        boolean flushArgument;
        boolean exit;

        while (lines.hasNext()) {
            var line = lines.next();

            flushValue = false;
            flushArgument = false;
            exit = false;

            if (line.equals(END_ITEM)) {
                flushValue = true;
                flushArgument = true;
            }
            else if (line.equals(END_FILE)) {
                flushValue = true;
                flushArgument = true;
                exit = true;
            }
            else if (line.isEmpty()) {
                flushValue = true;
            }
            else {
                valueLines.add(line);
            }

            if (flushValue) {
                var value = String.join("\n", valueLines);

                valueLines.clear();

                fieldValues.add(value);
            }

            if (flushArgument) {
                if (fieldValues.size() < fieldNames.size()) {
                    throw new AssertionError("Missing value for: " + fieldNames.get(fieldValues.size()));
                }
                else if (fieldValues.size() > fieldNames.size()) {
                    throw new AssertionError("Too much values, expected only: " + fieldNames);
                }
                else {
                    arguments.add(Arguments.of(fieldValues.toArray()));
                }

                fieldValues.clear();
            }

            if (exit) {
                break;
            }
        }
    }

}
