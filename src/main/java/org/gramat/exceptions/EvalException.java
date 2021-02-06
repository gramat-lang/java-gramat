package org.gramat.exceptions;

import org.gramat.exceptions.reporting.ErrorDetail;

public class EvalException extends GramatException {

    private final Integer actionID;
    private final Integer nodeID;

    public EvalException(String message, Integer actionID, Integer nodeID) {
        super(message);
        this.actionID = actionID;
        this.nodeID = nodeID;
    }

    public EvalException(String message, ErrorDetail detail, Integer actionID, Integer nodeID) {
        super(message, detail);
        this.actionID = actionID;
        this.nodeID = nodeID;
    }

    public Integer getActionID() {
        return actionID;
    }

    public Integer getNodeID() {
        return nodeID;
    }
}
