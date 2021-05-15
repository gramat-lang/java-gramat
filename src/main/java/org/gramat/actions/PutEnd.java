package org.gramat.actions;

public class PutEnd implements Action {
    public final String nameHint;

    PutEnd(String nameHint) {
        this.nameHint = nameHint;
    }

    @Override
    public String toString() {
        if (nameHint != null) {
            return String.format("put-end(%s)", nameHint);
        }
        return "put-end";
    }
}
