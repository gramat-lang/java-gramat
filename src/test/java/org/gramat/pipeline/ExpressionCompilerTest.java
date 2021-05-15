package org.gramat.pipeline;

import org.gramat.expressions.ExpressionProgram;
import org.gramat.tools.CharInput;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

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

        assertEquals(expected, actual);
    }

    static Stream<Arguments> testExpression() {
        return Stream.of(
                Arguments.of(
                        "Alternation",
                        "'a'|'b'|'c'",
                        """
                        ->1
                        1->2 : a
                        1->2 : b
                        1->2 : c
                        2<=
                        """
                ),
                Arguments.of(
                        "Option",
                        "['a']",
                        """
                        ->1
                        1->2 : a
                        1->2
                        2<=
                        """),
                Arguments.of(
                        "Repeat",
                        "{+'a'}",
                        """
                        ->1
                        1->2 : a
                        2->2 : a
                        2<=
                        """),
                Arguments.of(
                        "Repeat with separator",
                        "{+'a'/'b'}",
                        """
                        ->1
                        1->2 : a
                        2->3 : b
                        3->2 : a
                        2<=
                        """),
                Arguments.of(
                        "Sequence",
                        "'a' 'b' 'c'",
                        """
                        ->1
                        1->3 : a
                        3->4 : b
                        4->2 : c
                        2<=
                        """),
                Arguments.of(
                        "Literal",
                        "'a'", """
                        ->1
                        1->2 : a
                        2<=
                        """)
        );
    }

}
