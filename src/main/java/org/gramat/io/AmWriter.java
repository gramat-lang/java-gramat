package org.gramat.io;

import org.gramat.errors.ErrorFactory;

import java.io.IOException;

public class AmWriter {

    private final Appendable output;

    public AmWriter(Appendable output) {
        this.output = output;
    }

    public void writeAccepted(String state) {
        writeName(state);
        write("<=");
        writeNewLine();
    }

    public void writeInitial(String state) {
        write("->");
        writeName(state);
        writeNewLine();
    }

    public void writeTransition(String source, String target, String label) {
        writeName(source);
        write("->");
        writeName(target);
        if (label != null && !label.isBlank()) {
            write(" : ");
            writeLabel(label);
        }
        writeNewLine();
    }

    public void writeComment(String message) {
        write("# ");
        write(message);
        writeNewLine();
    }

    private void writeLabel(String label) {
        write(label
                .replace("\\", "\\\\")
                .replace("\n", "\\\n")
                .replace("!", "\\!")
                .replace(",", "\\,"));
    }

    private void writeName(String name) {
        write(name);
    }

    private void writeNewLine() {
        write("\n");
    }

    private void write(String value) {
        try {
            output.append(value);
        }
        catch (IOException e) {
            throw ErrorFactory.internalError(e);
        }
    }
}
