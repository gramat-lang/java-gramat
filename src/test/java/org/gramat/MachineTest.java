package org.gramat;

import org.gramat.pipeline.MachineFormatter;
import org.gramat.tools.CharInput;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tools.ArgumentsParser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MachineTest {

    @ParameterizedTest
    @MethodSource
    void testExpressionVsMachine(String title, String expressionInput, String expected) {
        var gramat = new Gramat();
        var input = CharInput.of(expressionInput, title);
        var machine = gramat.compile(input);
        var formatter = new MachineFormatter();
        var actual = formatter.writeMachine(machine);

        assertEquals(expected + "\n", actual);
    }

    static List<Arguments> testExpressionVsMachine() {
        return ArgumentsParser.parse(
                "/machine-tests/00-plain-single.txt",
                "/machine-tests/01-plain-mixed.txt",
                "/machine-tests/02-not-recursive.txt"
        );
    }

}
