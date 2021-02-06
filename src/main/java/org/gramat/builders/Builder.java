package org.gramat.builders;

import org.gramat.eval.EvalEngine;

public interface Builder {

    Object build(EvalEngine engine);

    void accept(Object value);

}
