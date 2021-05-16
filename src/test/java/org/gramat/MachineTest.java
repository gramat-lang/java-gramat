package org.gramat;

import lombok.extern.slf4j.Slf4j;
import org.gramat.pipeline.MachineFormatter;
import org.gramat.tools.CharInput;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tools.AmEditor;
import tools.ArgumentsParser;

import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MachineTest {

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
                "/machine-tests/00-plain-single.txt",
                "/machine-tests/01-plain-mixed.txt",
                "/machine-tests/02-lineal-refs.txt",
                "/machine-tests/03-recursive-refs.txt"
        );
    }

}
