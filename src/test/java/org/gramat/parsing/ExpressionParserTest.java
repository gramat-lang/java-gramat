package org.gramat.parsing;

import lombok.extern.slf4j.Slf4j;
import org.gramat.pipeline.ExpressionCompiler;
import org.gramat.pipeline.ExpressionExpander;
import org.gramat.pipeline.ExpressionParser;
import org.gramat.pipeline.MachineFormatter;
import org.gramat.pipeline.MachineLinker;
import org.gramat.tools.CharInput;
import org.junit.jupiter.api.Test;
import tools.Resources;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class ExpressionParserTest {

    @Test
    void test() {
        var resources = List.of(
//                "/json.gm",
//                "/sql.gm",
                "/test.gm");

        for (var resource : resources) {
            log.debug("parsing {}...", resource);

            var parser = new ExpressionParser();
            var input = CharInput.of(Resources.loadString(resource), resource);

            var map = parser.parseFile(input);

            var program = ExpressionExpander.run(map, "main");

            var machineFormatter = new MachineFormatter();
            machineFormatter.ignoreActions = true;

            var machineProgram = ExpressionCompiler.run(program);

            machineFormatter.writeProgram(System.out, machineProgram);

            var machineContract = MachineLinker.run(machineProgram);

            machineFormatter.writeMachine(System.out, machineContract);

            assertNotNull(machineProgram);
        }
    }

}
