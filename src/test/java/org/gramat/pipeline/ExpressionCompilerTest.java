package org.gramat.pipeline;

import org.gramat.expressions.ExpressionProgram;
import org.gramat.tools.CharInput;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tools.ArgumentsParser;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionCompilerTest {

    @ParameterizedTest
    @MethodSource
    void testExpression(String title, String expressionInput, String expected) {
        var input = CharInput.of(expressionInput, title);
        var expression = ExpressionParser.parseExpression(input);
        var expressionProgram = new ExpressionProgram(expression, Map.of());
        var machineProgram = ExpressionCompiler.run(expressionProgram);

        assertTrue(machineProgram.dependencies.isEmpty(), "Dependencies map must be empty");

        var formatter = new MachineFormatter();
        var actual = formatter.writeMachine(machineProgram.main);

        assertEquals(expected + "\n", actual);
    }

    static List<Arguments> testExpression() {
        return ArgumentsParser.parse("/org/gramat/pipeline/ExpressionCompilerTest.txt");
    }

}
