package org.gramat.util;

import org.gramat.exceptions.GramatException;

import java.io.IOException;

public class PP {

    // TODO add max length

    public static String str(Object any) {
        var out = new StringBuilder();

        str(any, out);

        return out.toString();
    }

    public static void str(Object any, Appendable out) {
        try {
            if (any == null) {
                out.append("null");
            } else if (any instanceof CharSequence) {
                qtd((CharSequence) any, out);
            } else if (any instanceof Character) {
                qtd(any.toString(), out);
            } else if (any instanceof Iterable) {
                lst((Iterable<?>) any, out);
            }
            else {
                raw(any.toString(), out);
            }
        }
        catch (IOException e) {
            throw new GramatException("str exception", e);
        }
    }

    private static void raw(String str, Appendable out) throws IOException {
        // TODO handle special cases like line breaks, long string, etc.
        out.append(str);
    }

    private static void qtd(CharSequence value, Appendable out) throws IOException {
        out.append('"');

        for (var i = 0; i < value.length(); i++) {
            var c = value.charAt(i);

            if (c == '\n') {
                out.append("\\n");
            }
            else if (c == '\t') {
                out.append("\\t");
            }
            else if (c == '\r') {
                out.append("\\r");
            }
            else if (c == '\0') {
                out.append("\\0");
            }
            else if (c == '\"' || c == '\'' || c == '\\') {
                out.append('\\');
                out.append(c);
            }
            // TODO improve this
            else {
                out.append(c);
            }
        }

        out.append('"');
    }

    private static void lst(Iterable<?> items, Appendable out) throws  IOException {
        out.append('[');

        int i = 0;
        for (var item : items) {
            if (i > 0) {
                out.append(", ");
            }

            str(item, out);

            i++;
        }

        out.append(']');
    }

    private PP() {}

}
