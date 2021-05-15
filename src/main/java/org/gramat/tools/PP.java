package org.gramat.tools;

public class PP {

    public static String ch(char c) {
        if (c >= 0x21 && c <= 0x7E) {
            return String.valueOf(c);
        }

        switch (c) {
            case ' ': return "\\s";
            case '\n': return "\\n";
            case '\t': return "\\t";
            case '\r': return "\\r";
            default: return "0x" + Integer.toHexString(c);
        }
    }

    private PP() {}

}
