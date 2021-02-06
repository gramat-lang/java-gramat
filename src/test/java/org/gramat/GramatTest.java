package org.gramat;

import org.gramat.exceptions.EvalException;
import org.gramat.exceptions.GramatException;
import org.gramat.inputs.InputCharSequence;

import org.gramat.logging.PrintStreamLogger;
import org.gramat.util.Debug;
import org.gramat.util.PP;
import org.junit.jupiter.api.Test;
import util.TestUtils;

class GramatTest {

    @Test
    void test() {
        var code = TestUtils.loadString("/json.gm");
        var logger = new PrintStreamLogger(System.out);
        var gramat = new Gramat(logger);
        var program = gramat.compile(new InputCharSequence(code));

        try {
            var result = gramat.eval(program.node, new InputCharSequence("{\"a\":[ true, false]}"));

            logger.debug("RESULT: %s", PP.str(result));
        }
        catch (EvalException e) {
            Debug.print(System.err, e, program);
        }
    }

}
