package org.gramat;

import lombok.extern.slf4j.Slf4j;
import org.gramat.pipeline.MachineFormatter;
import org.gramat.tools.CharInput;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
        var machine = gramat.compile(input);
        var formatter = new MachineFormatter();
        var actual = formatter.writeMachine(machine);

        if (!actual.equals(expected + "\n")) {
            var actualUrl = generateAmEditorUrl(actual);
            var expectedUrl = generateAmEditorUrl(expected);

            log.info("  Actual: {}", actualUrl);
            log.info("Expected: {}", expectedUrl);

            fail("Error: " + title);
        }
    }

    private String generateAmEditorUrl(String actual) {
        var data = actual.getBytes(StandardCharsets.UTF_8);
        var base64 = Base64.getEncoder().encodeToString(data);
        var param = URLEncoder.encode(base64, StandardCharsets.UTF_8);
        return "https://sergiouph.github.io/am-editor/?dir=LR&input=" + param;
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
