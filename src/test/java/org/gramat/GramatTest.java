package org.gramat;

import lombok.extern.slf4j.Slf4j;
import org.gramat.pipeline.ExpressionCompiler;
import org.gramat.pipeline.ExpressionExpander;
import org.gramat.pipeline.ExpressionParser;
import org.gramat.pipeline.MachineFormatter;
import org.gramat.pipeline.MachineLinker;
import org.gramat.tools.CharInput;
import org.junit.jupiter.api.Test;
import tools.AmEditor;
import tools.Resources;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class GramatTest {

    @Test
    void test() {
        var resources = List.of(
//                "/json.gm",
//                "/sql.gm",
                "/test.gm");

        for (var resource : resources) {
            var input = CharInput.of(Resources.loadString(resource), resource);
            var gramat = new Gramat();
            var machine = gramat.compile(input);

            log.info("  Actual: {}", AmEditor.url(machine));
            log.info("Expected: {}", AmEditor.url(machine));
        }
    }

}
