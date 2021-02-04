package org.gramat.automating;

public class Level {

    public static final Level ANY = new Level(0);

    public final int id;

    public Level(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        if (this == ANY) {
            return "Level(*)";
        }
        return "Level(" + id + ')';
    }
}
