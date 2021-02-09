package org.gramat;

import org.gramat.exceptions.EvalException;
import org.gramat.exceptions.GramatException;
import org.gramat.inputs.InputCharSequence;

import org.gramat.logging.PrintStreamLogger;
import org.gramat.util.Debug;
import org.gramat.util.PP;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.TestUtils;

class GramatTest {

    @Test
    void runTests() {
//        runTest("/json.gm",
//                "{\"a\":[ true, false]}");
        runTest("/test.gm",
                "(x=[x,(),[]],x=x)");
    }

    private static void runTest(String resource, String... inputs) {
        var code = TestUtils.loadString(resource);
        var logger = new PrintStreamLogger(System.out);
        var gramat = new Gramat(logger);
        var program = gramat.compile(new InputCharSequence(code));

        for (var input : inputs) {
            try {
                var result = gramat.eval(program.node, new InputCharSequence(input));

                logger.debug("RESULT: %s", PP.str(result));
            }
            catch (EvalException e) {
                Debug.print(System.err, e, program);
                Assertions.fail(e.getMessage());
            }
        }
    }

}
