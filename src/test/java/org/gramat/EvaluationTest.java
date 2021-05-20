package org.gramat;

import lombok.extern.slf4j.Slf4j;
import org.gramat.tools.CharInput;
import org.junit.jupiter.api.Test;
import tools.AmEditor;
import tools.Resources;

import java.util.List;

@Slf4j
class EvaluationTest {

    @Test
    void test() {
        var resources = List.of(
//                "/EvaluationTest/json.gm",
//                "/EvaluationTest/sql.gm",
                "/EvaluationTest/test.gm");

        for (var resource : resources) {
            var input = CharInput.of(Resources.loadString(resource), resource);
            var gramat = new Gramat();
            var automaton = gramat.compile(input);

            log.info("Automaton: {}", AmEditor.url(automaton));
        }
    }

}
