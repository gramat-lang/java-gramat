package org.gramat.formatting;

import org.gramat.exceptions.GramatException;
import org.gramat.expressions.Expression;
import org.gramat.expressions.engines.ParsingEngine;
import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.groups.Cycle;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.literals.LiteralChar;
import org.gramat.expressions.literals.LiteralRange;
import org.gramat.expressions.misc.ActionExpression;
import org.gramat.expressions.misc.Halt;
import org.gramat.expressions.misc.Reference;
import org.gramat.expressions.misc.Wild;

import java.io.IOException;
import java.util.regex.Pattern;

public class ExpressionFormatter {

    public static String toString(Expression expr) {
        var buffer = new StringBuilder();

        new ExpressionFormatter(buffer).write(expr);

        return buffer.toString();
    }

    private final Appendable out;

    private int maxWidth;

    private int width;

    public ExpressionFormatter(Appendable out) {
        this.out = out;
        this.maxWidth = 80;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void write(Expression expr) {
        write(expr, false);
    }

    public void write(Expression expr, boolean wrapped) {
        if (expr instanceof LiteralChar) {
            writeLiteralChar((LiteralChar)expr);
        }
        else if (expr instanceof LiteralRange) {
            writeLiteralRange((LiteralRange)expr);
        }
        else if (expr instanceof Alternation) {
            writeAlternation((Alternation)expr, wrapped);
        }
        else if (expr instanceof Optional) {
            writeOptional((Optional)expr);
        }
        else if (expr instanceof Cycle) {
            writeCycle((Cycle)expr);
        }
        else if (expr instanceof Sequence) {
            writeSequence((Sequence)expr, wrapped);
        }
        else if (expr instanceof ActionExpression) {
            writeAction((ActionExpression)expr);
        }
        else if (expr instanceof Reference) {
            writeReference((Reference)expr);
        }
        else if (expr instanceof Wild) {
            writeWild();
        }
        else if (expr instanceof Halt) {
            writeHalt();
        }
        else {
            throw new GramatException("unsupported value: " + expr);
        }
    }

    private void writeLiteralChar(LiteralChar expr) {
        writeRaw('\"');
        writeCharItem(expr.value);
        writeRaw('\"');
        wbr();
    }

    private void writeLiteralRange(LiteralRange expr) {
        writeRaw('\'');
        if (expr.begin == ' ') {
            writeRaw("\\s");
        }
        else {
            writeCharItem(expr.begin);
        }
        writeRaw('-');
        if (expr.end == ' ') {
            writeRaw("\\s");
        }
        else {
            writeCharItem(expr.end);
        }
        writeRaw('\'');
        wbr();
    }

    private void writeAlternation(Alternation expr, boolean wrapped) {
        if (wrapped) {
            writeRaw('(');
            wbr();
        }

        for (int i = 0; i < expr.items.size(); i++) {
            if (i > 0) {
                wbr();
                writeRaw('|');
                wbr();
            }

            write(expr.items.get(i), true);
        }

        if (wrapped) {
            wbr();
            writeRaw(')');
            wbr();
        }
    }

    private void writeOptional(Optional expr) {
        writeRaw('[');
        wbr();

        write(expr.content, false);

        wbr();
        writeRaw(']');
        wbr();
    }

    private void writeCycle(Cycle expr) {
        writeRaw('{');
        writeRaw('+');
        wbr();

        write(expr.content, false);

        wbr();
        writeRaw('}');
        wbr();
    }

    private void writeSequence(Sequence expr, boolean wrapped) {
        if (wrapped) {
            writeRaw('(');
            wbr();
        }

        for (int i = 0; i < expr.items.size(); i++) {
            if (i > 0) {
                wbr();
                writeRaw(' ');
            }

            write(expr.items.get(i), true);
        }

        if (wrapped) {
            wbr();
            writeRaw(')');
            wbr();
        }
    }

    private void writeWild() {
        writeRaw('*');
    }

    private void writeHalt() {
        writeRaw('^');
    }

    private void writeReference(Reference expr) {
        writeKeyword(expr.name);
    }

    private void writeAction(ActionExpression action) {
        var symbol = ParsingEngine.findActionChar(action.scheme);

        writeRaw('<');
        writeRaw(symbol);

        if (action.argument != null) {
            writeKeyword(action.argument);
        }

        writeRaw('(');
        wbr();
        write(action.content, false);
        wbr();
        writeRaw(')');
        wbr();

        writeRaw(symbol);
        writeRaw('>');
    }

    private void writeKeyword(String keyword) {
        if (Pattern.matches("[a-zA-Z][a-zA-Z0-9_-]*", keyword)) {
            writeRaw(keyword);
        }
        else {
            writeRaw('`');
            for (var c : keyword.toCharArray()) {
                writeCharItem(c);
            }
            writeRaw('`');
        }
    }

    private void writeCharItem(char c) {
        switch(c) {
            case '\n': writeRaw("\\n"); break;
            case '\r': writeRaw("\\r"); break;
            case '\t': writeRaw("\\t"); break;
            case '"':
            case '`':
            case '\'':
            case '\\':
                writeRaw("\\");
                writeRaw(c);
                break;
            default:
                writeRaw(c);
                break;
        }
    }

    private void wbr() {
        if (width >= maxWidth) {
            writeRaw('\n');
        }
    }

    private void writeRaw(String s) {
        for (var c : s.toCharArray()) {
            writeRaw(c);
        }
    }

    private void writeRaw(char c) {
        try {
            out.append(c);
            if (c == '\n') {
                width = 0;
            }
            else {
                width++;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
