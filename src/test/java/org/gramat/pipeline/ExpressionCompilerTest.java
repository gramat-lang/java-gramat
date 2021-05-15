package org.gramat.pipeline;

import org.gramat.expressions.ExpressionProgram;
import org.gramat.machines.Machine;
import org.gramat.tools.CharInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionCompilerTest {

    @Test
    void testAlternation(TestInfo testInfo) {
        var actual = compile("'a'|'b'", testInfo);

        var expected = """
                ->1
                1->2 : a
                1->2 : b
                2<=
                """;

        assertEquals(expected, actual);
    }

    private static String compile(String expressionInput, TestInfo testInfo) {
        var input = CharInput.of(expressionInput, testInfo.getDisplayName());
        var expression = ExpressionParser.parseExpression(input);
        var expressionProgram = new ExpressionProgram(expression, Map.of());
        var machineProgram = ExpressionCompiler.run(expressionProgram);

        assertTrue(machineProgram.dependencies.isEmpty(), "Dependencies map must be empty");

        var formatter = new MachineFormatter();

        return formatter.writeMachine(machineProgram.main);
    }

}
