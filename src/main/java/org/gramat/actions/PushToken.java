package org.gramat.actions;

public class PushToken implements Action {

    public final String token;

    public PushToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "push(" + token + ")";
    }
}
