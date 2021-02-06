package org.gramat.builders;

import org.gramat.eval.EvalEngine;

public interface Builder {

    void acceptMetadata(String name, Object value);
    void acceptContent(Object value);

    Object build(EvalEngine engine);

}
