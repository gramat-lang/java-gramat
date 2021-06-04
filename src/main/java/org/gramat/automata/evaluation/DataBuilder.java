package org.gramat.automata.evaluation;

import lombok.extern.slf4j.Slf4j;
import org.gramat.automata.containers.Container;
import org.gramat.automata.containers.ContainerKey;
import org.gramat.automata.containers.ContainerPut;

import java.util.ArrayDeque;
import java.util.Deque;

@Slf4j
public class DataBuilder {
    private final Deque<Container> data;

    public DataBuilder() {
        data = new ArrayDeque<>();
    }

    public void beginPut() {
        log.debug("beginPut");

        data.push(new ContainerPut());
    }

    public void beginKey() {
        log.debug("beginKey");

        data.push(new ContainerKey());
    }

    public void endKey() {
        log.debug("endKey");

        var containerKey = popContainer(ContainerKey.class);
        var containerPut = peekContainer(ContainerPut.class);

        containerPut.setKey(containerKey.getValue());
    }

    public void endPut(String nameHint) {
        log.debug("endPut");
    }

    public void beginList() {
        log.debug("beginList");
    }

    public void endList(String typeHint) {
        log.debug("endList");
    }

    public void beginMap() {
        log.debug("beginMap");
    }

    public void endMap(String typeHint) {
        log.debug("endMap");
    }

    public void beginValue() {
        log.debug("beginValue");
    }

    public void endValue(String typeHint) {
        log.debug("endValue");
    }



    private <T extends Container> T popContainer(Class<T> containerType) {
        var c = data.pop();
        if (containerType.isInstance(c)) {
            return containerType.cast(c);
        }
        else {
            throw new RuntimeException();
        }
    }

    private <T extends Container> T peekContainer(Class<T> containerType) {
        var c = data.peek();
        if (containerType.isInstance(c)) {
            return containerType.cast(c);
        }
        else {
            throw new RuntimeException();
        }
    }
}
