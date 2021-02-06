package org.gramat.inputs;

import org.gramat.exceptions.GramatException;

public class InputCharSequence implements Input {

    private final CharSequence sequence;

    private int offset;
    private int line;
    private int column;

    public InputCharSequence(CharSequence sequence) {
        this.sequence = sequence;
    }

    @Override
    public char peek() {
        if (offset >= sequence.length()) {
            return '\0';
        }
        return sequence.charAt(offset);
    }

    @Override
    public char pull() {
        if (offset >= sequence.length()) {
            throw new GramatException("unexpected EOF");
        }
        var c = sequence.charAt(offset);

        if (c == '\n') {
            line++;
            column = 0;
        }
        else {
            column++;
        }

        offset++;
        return c;
    }

    @Override
    public boolean alive() {
        return offset < sequence.length();
    }

    @Override
    public String segment(int begin, int end) {
        return sequence.subSequence(begin, end).toString();
    }

    @Override
    public int getPosition() {
        return offset;
    }

    @Override
    public Location getLocation() {
        return new Location(null, offset, line + 1, column + 1);
    }
}
