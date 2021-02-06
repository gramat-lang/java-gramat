package org.gramat.expressions.engines;

public class Syntax {

    // Char classes

    public static boolean isBlockVoid(char c) {
        return isInlineVoid(c) || c == '\n' || c == '\r';
    }

    public static boolean isInlineVoid(char c) {
        return c == ' ' || c == '\t';
    }

    public static boolean isKeywordBegin(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    public static boolean isKeywordContent(char c) {
        return isKeywordBegin(c) || c == '-' || c == '_' || (c >= '0' && c <= '9');
    }

}
