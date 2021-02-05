package org.gramat;

import org.gramat.inputs.InputCharSequence;

import org.gramat.logging.PrintStreamLogger;
import org.junit.jupiter.api.Test;
import util.TestUtils;

class GramatTest {

    @Test
    void test() {
        var code = TestUtils.loadString("/test.gm");
        var logger = new PrintStreamLogger(System.out);
        var gramat = new Gramat(logger);
        var node = gramat.compile(new InputCharSequence(code));

        gramat.eval(node, new InputCharSequence("(x=[x,x,()],x=x,x=(x=[x,x,x]))"));
    }

}
