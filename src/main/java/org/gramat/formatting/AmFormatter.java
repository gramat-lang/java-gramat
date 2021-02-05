package org.gramat.formatting;

import org.gramat.exceptions.GramatException;

import java.io.IOException;
import java.util.List;

public class AmFormatter {
    private final Appendable out;

    public AmFormatter(Appendable out) {
        this.out = out;
    }

    public void writeInitial(String state) {
        writeRaw("-> ");
        writeStateName(state);
        writeRaw('\n');
    }

    public void writeAccepted(String state) {
        writeStateName(state);
        writeRaw("<=\n");
    }

    public void writeTransition(String source, String target, List<String> symbols) {
        writeStateName(source);
        writeRaw(" -> ");
        writeStateName(target);
        if (symbols != null && !symbols.isEmpty()) {
            writeRaw(" : ");
            for (int i = 0; i < symbols.size(); i++) {
                if (i > 0) {
                    writeRaw(", ");
                }

                writeSymbol(symbols.get(i));
            }
        }
        writeRaw('\n');
    }

    private void writeSymbol(String symbol) {
        writeRaw(symbol
                .replace(":", "\\:")
                .replace("!", "\\!")
                .replace(",", "\\,")
                .replace("\n", "\\\\n"));
    }

    private void writeStateName(String state) {
        // TODO escape state name
        writeRaw(state);
    }

    private void writeRaw(char c) {
        try {
            out.append(c);
        }
        catch (IOException e) {
            throw new GramatException("output error", e);
        }
    }

    private void writeRaw(String text) {
        try {
            out.append(text);
        }
        catch (IOException e) {
            throw new GramatException("output error", e);
        }
    }
}
