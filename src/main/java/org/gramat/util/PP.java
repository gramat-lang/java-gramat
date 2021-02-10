package org.gramat.util;

import org.gramat.exceptions.GramatException;
import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionChildren;
import org.gramat.expressions.ExpressionContent;
import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.groups.Cycle;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.literals.LiteralChar;
import org.gramat.expressions.literals.LiteralRange;
import org.gramat.expressions.misc.ActionExpression;
import org.gramat.expressions.misc.Halt;
import org.gramat.expressions.misc.Nop;
import org.gramat.expressions.misc.Reference;
import org.gramat.expressions.misc.Wild;

import java.io.IOException;
import java.sql.Ref;

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
            }
            else if (any instanceof Expression) {
                exp((Expression) any, out);
            }
            else if (any instanceof CharSequence) {
                qtd((CharSequence) any, out);
            }
            else if (any instanceof Character) {
                qtd(any.toString(), out);
            }
            else if (any instanceof Iterable) {
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

    private static void exp(Expression expression, Appendable out) throws IOException {
        if (expression instanceof ActionExpression) {
            var a = (ActionExpression) expression;

            out.append("Action:");
            out.append(a.scheme.name());
            out.append("(");
            if (a.argument != null) {
                out.append(a.argument);
                out.append(": ");
            }
            out.append(a.getContent().getClass().getSimpleName());
            out.append(")");
        }
        else if (expression instanceof Reference) {
            var r = (Reference) expression;

            out.append("Reference(");
            out.append(r.name);
            out.append(")");
        }
        else if (expression instanceof LiteralChar) {
            var c = (LiteralChar) expression;

            out.append("Literal(");
            out.append(PP.str(c.value));
            out.append(")");
        }
        else if (expression instanceof LiteralRange) {
            var c = (LiteralRange) expression;

            out.append("Literal(");
            out.append(PP.str(c.begin));
            out.append("-");
            out.append(PP.str(c.end));
            out.append(")");
        }
        else if (expression instanceof ExpressionChildren) {
            var ec = (ExpressionChildren) expression;
            var size = ec.getItems().size();

            out.append(expression.getClass().getSimpleName());
            out.append("(");
            if (size == 1) {
                out.append("1 item");
            }
            else {
                out.append(String.valueOf(size));
                out.append(" items");
            }
            out.append(")");
        }
        else if (expression instanceof ExpressionContent) {
            var ec = (ExpressionContent) expression;

            out.append(expression.getClass().getSimpleName());
            out.append("(");
            out.append(ec.getContent().getClass().getSimpleName());
            out.append(")");
        }
        else {
            out.append(expression.toString());
        }
    }

    private PP() {}

}
