package org.gramat.actions;

public class PopToken implements Action {
    public final String token;

    public PopToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "pop(" + token + ")";
    }
}
