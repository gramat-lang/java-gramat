package tools.args;

public class LineReader {

    private final String[] lines;

    private int position;

    public LineReader(String[] lines) {
        this.lines = lines;
    }

    public boolean isOpen() {
        return position < lines.length;
    }

    public String peek() {
        if (position >= lines.length) {
            throw new RuntimeException("EOF");
        }
        return lines[position];
    }

    public void move() {
        if (position >= lines.length) {
            throw new RuntimeException("EOF");
        }
        position++;
    }

    public String pull() {
        if (position >= lines.length) {
            throw new RuntimeException("EOF");
        }
        var line = lines[position];
        position++;
        return line;
    }

    public boolean pull(String line) {
        if (position >= lines.length || !lines[position].equals(line)) {
            return false;
        }
        position++;
        return true;
    }

    public void skipEmptyLines() {
        while (position < lines.length) {
            if (lines[position].isEmpty()) {
                position++;
            }
            else {
                break;
            }
        }
    }

    public int getLineNumber() {
        return position + 1;
    }
}
