package org.gramat.tools;

import java.util.concurrent.atomic.AtomicInteger;

public interface IdentifierProvider {

    static IdentifierProvider create(int begin) {
        var atomic = new AtomicInteger(begin);

        return atomic::getAndIncrement;
    }

    int next();

}
