package org.gramat;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.Automaton;
import org.gramat.automata.evaluation.Evaluator;
import org.gramat.automata.tapes.Tape;
import org.gramat.io.AutomatonFormatter;
import org.gramat.io.ExpressionFormatter;
import org.gramat.io.MachineFormatter;
import org.gramat.machine.nodes.NodeFactory;
import org.gramat.pipeline.AutomatonCompiler;
import org.gramat.pipeline.ExpressionCompiler;
import org.gramat.pipeline.ExpressionExpander;
import org.gramat.pipeline.ExpressionParser;
import org.gramat.pipeline.MachineCompiler;
import org.gramat.tools.CharInput;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tools.AmEditor;
import tools.DataLink;
import tools.args.ArgGroup;
import tools.args.ArgParser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@Slf4j
class AutomatonTest {

    @ParameterizedTest
    @MethodSource
    void testExpressionVsMachine(ArgGroup args) {
        log.info("Test case ({}:{})", Path.of(args.getResource()).getFileName(), args.getLineNumber());

        assumeFalse(args.isIgnored());

        var title = args.getValue("title");
        var expression = args.getValue("expression");
        var expected = args.getValue("automaton").trim();

        var automaton = compile(expression, title);
        var actual = AutomatonFormatter.toString(automaton).trim();

        log.info("  Actual: {}", AmEditor.url(actual));
        log.info("Expected: {}", AmEditor.url(expected));

        assertEquals(expected, actual);

        for (var entry : args) {
            if ("pass".equals(entry.getName())) {
                var passInput = entry.getValue();

                log.info("Evaluating {}", passInput);

                var evaluator = new Evaluator();
                var result = evaluator.eval(automaton.getInitial(), Tape.of(passInput, "pass"));

                log.info("Passed! result: {}", result);
            }
            else if ("fail".equals(entry.getName())) {
                var failInput = entry.getName();
                log.info("Evaluating {}", failInput);

                var evaluator = new Evaluator();

                boolean failed;

                try {
                    var result = evaluator.eval(automaton.getInitial(), Tape.of(failInput, "fail"));

                    failed = true;
                }
                catch (Exception e) {
                    failed = false;

                    log.info("Failed (expected): {}", e.getMessage());
                }

                if (failed) {
                    throw new AssertionError("Not expected to pass: " + failInput);
                }
            }
        }
    }

    static Automaton compile(String text, String resource) {
        var input = CharInput.of(text, resource);
        var map = ExpressionParser.parseFile(input);
        var expressionProgram = ExpressionExpander.run(map, "main");

        log.info("Expanded expression: {}", DataLink.of("expanded-expression", ExpressionFormatter.toString(expressionProgram, "main")));

        var nodeProvider = new NodeFactory();
        var machineProgram = ExpressionCompiler.run(nodeProvider, expressionProgram);

        log.info("Compiled expression: {}", DataLink.of("compiled-expression", MachineFormatter.toString(machineProgram)));

        var machine = MachineCompiler.run(nodeProvider, machineProgram);

        log.info("Compiled machine: {}", DataLink.of("compiled-machine", MachineFormatter.toString(machine)));

        var automaton = AutomatonCompiler.run(machine);

        log.info("Automaton: {}", DataLink.of("automaton", AutomatonFormatter.toString(automaton)));

        return automaton;
    }

    static List<Arguments> testExpressionVsMachine() {
        var result = new ArrayList<Arguments>();
        var session = ArgParser.parse(
                "/AutomatonTest/plain-single.txt",
                "/AutomatonTest/plain-mixed.txt",
                "/AutomatonTest/plain-references.txt",
                "/AutomatonTest/actions-simple.txt",
                "/AutomatonTest/recursive-simple.txt",
                "/AutomatonTest/recursive-complex.txt"
        );

        for (var group : session) {
            result.add(Arguments.of(group));
        }

        return result;
    }

}
