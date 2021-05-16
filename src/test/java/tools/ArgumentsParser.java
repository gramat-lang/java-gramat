package tools;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.provider.Arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ArgumentsParser {

    private static final String END_ITEM = "----------";
    private static final String END_SPECIAL = "----------^";

    public static List<Arguments> parse(String... resources) {
        var arguments = new ArrayList<Arguments>();
        var specials = new ArrayList<Arguments>();

        for (var resource : resources) {
            log.debug("Reading arguments from {}...", resource);

            readResource(resource, arguments, specials);
        }

        log.debug("Reading arguments completed: {} set(s)", arguments.size());

        if (specials.isEmpty()) {
            return arguments;
        }
        else {
            log.debug("Ignoring read arguments but: {} special set(s)", specials.size());
            return specials;
        }
    }

    private static void readResource(String resource, List<Arguments> arguments, List<Arguments> specials) {
        var lines = Arrays.stream(Resources.loadLines(resource)).iterator();
        var valueLines = new ArrayList<String>();
        var fieldValues = new ArrayList<String>();

        boolean flushValue;
        boolean flushArgument;
        boolean special;

        while (lines.hasNext()) {
            var line = lines.next();

            flushValue = false;
            flushArgument = false;
            special = false;

            if (line.equals(END_ITEM)) {
                flushValue = true;
                flushArgument = true;
            }
            else if (line.equals(END_SPECIAL)) {
                flushValue = true;
                flushArgument = true;
                special = true;
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
                var args = Arguments.of(fieldValues.toArray());

                if (special) {
                    specials.add(args);
                }

                arguments.add(args);

                fieldValues.clear();
            }
        }

        if (!valueLines.isEmpty() || !fieldValues.isEmpty()) {
            throw new AssertionError("Missing end line.");
        }
    }

}
