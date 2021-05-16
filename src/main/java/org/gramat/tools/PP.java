package org.gramat.tools;

public class PP {

    public static String ch(char c) {
        if (c == '\'') {
            return "\"'\"";
        }

        if (c >= 0x20 && c <= 0x7E) {
            return "'" + c + "'";
        }

        return switch (c) {
            case '\n' -> "\\n";
            case '\t' -> "\\t";
            case '\r' -> "\\r";
            default -> "0x" + Integer.toHexString(c);
        };
    }

    private PP() {}
}
