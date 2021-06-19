package org.gramat.machine.links;

import org.gramat.machine.operations.Operation;
import org.gramat.machine.nodes.Node;
import org.gramat.machine.operations.OperationList;
import org.gramat.machine.patterns.Pattern;

import java.util.List;

public interface Link {

    Node getSource();
    Node getTarget();

    OperationList getBeginOperations();
    OperationList getEndOperations();

    boolean isEmpty();  // TODO replace by using instanceof?
    boolean hasPattern();  // TODO replace by using instanceof?

    Pattern getPattern(); // TODO replace by using instanceof?

}
