package org.gramat.formatting;

import org.gramat.exceptions.GramatException;
import org.gramat.expressions.Expression;
import org.gramat.expressions.actions.ListWrapper;
import org.gramat.expressions.actions.NameWrapper;
import org.gramat.expressions.actions.ObjectWrapper;
import org.gramat.expressions.actions.PropertyWrapper;
import org.gramat.expressions.actions.TextWrapper;
import org.gramat.expressions.engines.ActionFactory;
import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Repetition;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.literals.LiteralChar;
import org.gramat.expressions.literals.LiteralRange;
import org.gramat.expressions.literals.LiteralString;
import org.gramat.expressions.misc.Recursion;
import org.gramat.expressions.misc.Reference;
import org.gramat.expressions.misc.Wild;

import java.io.IOException;
import java.util.regex.Pattern;

public class ExpressionFormatter {

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
        else if (expr instanceof LiteralString) {
            writeLiteralString((LiteralString)expr);
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
        else if (expr instanceof Repetition) {
            writeRepetition((Repetition)expr);
        }
        else if (expr instanceof Sequence) {
            writeSequence((Sequence)expr, wrapped);
        }
        else if (expr instanceof ListWrapper) {
            writeListWrapper((ListWrapper)expr);
        }
        else if (expr instanceof ObjectWrapper) {
            writeObjectWrapper((ObjectWrapper)expr);
        }
        else if (expr instanceof PropertyWrapper) {
            writePropertyWrapper((PropertyWrapper)expr);
        }
        else if (expr instanceof TextWrapper) {
            writeTextWrapper((TextWrapper)expr);
        }
        else if (expr instanceof NameWrapper) {
            writeNameWrapper((NameWrapper)expr);
        }
        else if (expr instanceof Recursion) {
            writeRecursion((Recursion)expr);
        }
        else if (expr instanceof Reference) {
            writeReference((Reference)expr);
        }
        else if (expr instanceof Wild) {
            writeWild();
        }
        else {
            throw new GramatException("unsupported value: " + expr);
        }
    }

    private void writeNameWrapper(NameWrapper expr) {
        writeAction(ActionFactory.NAME_WRAPPER_ID, null, expr.content);
    }

    private void writeLiteralChar(LiteralChar expr) {
        writeRaw('\"');
        writeCharItem(expr.value);
        writeRaw('\"');
        wbr();
    }

    private void writeLiteralString(LiteralString expr) {
        writeRaw('\"');
        for (var c : expr.value.toCharArray()) {
            writeCharItem(c);
        }
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

    private void writeRepetition(Repetition expr) {
        writeRaw('{');
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

    private void writeListWrapper(ListWrapper expr) {
        writeAction(ActionFactory.LIST_WRAPPER_ID, expr.typeHint, expr.content);
    }

    private void writeObjectWrapper(ObjectWrapper expr) {
        writeAction(ActionFactory.OBJECT_WRAPPER_ID, expr.typeHint, expr.content);
    }

    private void writePropertyWrapper(PropertyWrapper expr) {
        writeAction(ActionFactory.PROPERTY_WRAPPER_ID, expr.nameHint, expr.content);
    }

    private void writeTextWrapper(TextWrapper expr) {
        writeAction(ActionFactory.TEXT_WRAPPER_ID, expr.parser, expr.content);
    }

    private void writeRecursion(Recursion expr) {
        writeKeyword(expr.name);
    }

    private void writeReference(Reference expr) {
        writeKeyword(expr.name);
    }

    private void writeAction(String id, String keyword, Expression content) {
        writeRaw('@');
        writeRaw(id);
        if (keyword != null) {
            writeRaw(':');
            writeKeyword(keyword);
        }
        if (content != null) {
            writeRaw('(');
            wbr();
            write(content, false);
            wbr();
            writeRaw(')');
            wbr();
        }
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
