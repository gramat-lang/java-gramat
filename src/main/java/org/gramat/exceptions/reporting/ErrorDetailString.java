package org.gramat.exceptions.reporting;

import java.io.PrintStream;

public class ErrorDetailString implements ErrorDetail {

    public static final int MAX_STRING_LENGTH = 1000;
    public static final int MAX_LINE_LENGTH = 80;

    private final String[] lines;

    public ErrorDetailString(String value) {
        this.lines = sanitize(value);
    }

    public String[] getLines() {
        return lines;
    }

    @Override
    public void printDetail(PrintStream out, int indentation) {
        for (String line : lines) {
            out.print("  ".repeat(indentation));
            out.print(line);
            out.println();
        }
    }

    public static String[] sanitize(String value) {
        if (value == null) {
            return new String[0];
        }

        boolean trimmed = false;

        if (value.length() > MAX_STRING_LENGTH) {
            // TODO validate this
            value = value.substring(MAX_STRING_LENGTH);
            trimmed = true;
        }

        value = value.trim();

        var lines = value.split("\r?\n");

        if (lines.length == 0) {
            lines = new String[]{""};
        }
        else {
            for (int i = 0; i < lines.length; i++) {
                var line = lines[i].trim();

                if (line.length() > MAX_LINE_LENGTH) {
                    line = line.substring(MAX_LINE_LENGTH);
                    line = line.trim() + "…";
                }

                lines[i] = (i < 10 ? "0" : "") + i + ": " + line;
            }
        }

        if (trimmed && !lines[lines.length-1].endsWith("…")) {
            lines[lines.length-1] = lines[lines.length-1] + "…";
        }

        return lines;
    }
}
