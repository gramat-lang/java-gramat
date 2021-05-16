package org.gramat;

import lombok.extern.slf4j.Slf4j;
import org.gramat.pipeline.MachineFormatter;
import org.gramat.tools.CharInput;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tools.AmEditor;
import tools.ArgumentsParser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class AutomatonTest {

    @ParameterizedTest
    @MethodSource
    void testExpressionVsMachine(String title, String expressionInput, String expected) {
        var gramat = new Gramat();
        var input = CharInput.of(expressionInput, title);
        var automaton = gramat.compile(input);
        var formatter = new MachineFormatter();
        var actual = formatter.writeAutomaton(automaton);

        if (!actual.equals(expected + "\n")) {
            log.info("  Actual: {}", AmEditor.url(
                    "# " + String.join("\n# ", expressionInput.split("\r?\n")) + "\n\n" + actual));
            log.info("Expected: {}", AmEditor.url(expected));

            fail("Error: " + title);
        }
    }

    static List<Arguments> testExpressionVsMachine() {
        return ArgumentsParser.parse(
                "/AutomatonTest/00-plain-single.txt",
                "/AutomatonTest/01-plain-mixed.txt",
                "/AutomatonTest/02-lineal-refs.txt",
                "/AutomatonTest/03-recursive-refs.txt",
                "/AutomatonTest/04-simple-actions.txt"
        );
    }

}
