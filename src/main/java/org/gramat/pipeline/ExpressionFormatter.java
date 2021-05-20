package org.gramat.pipeline;

import org.gramat.errors.ErrorFactory;
import org.gramat.expressions.Alternation;
import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionProgram;
import org.gramat.expressions.Literal;
import org.gramat.expressions.Option;
import org.gramat.expressions.Reference;
import org.gramat.expressions.Repeat;
import org.gramat.expressions.Sequence;
import org.gramat.expressions.Wildcard;
import org.gramat.expressions.Wrapping;
import org.gramat.symbols.SymbolChar;
import org.gramat.symbols.SymbolRange;

import java.io.IOException;

public class ExpressionFormatter {

    public void writeProgram(Appendable output, ExpressionProgram program, String mainRule) {
        writeRule(output, mainRule, program.main);

        for (var entry : program.dependencies.entrySet()) {
            writeRule(output, entry.getKey(), entry.getValue());
        }
    }

    public String writeProgram(ExpressionProgram program, String mainRule) {
        var output = new StringBuilder();

        writeProgram(output, program, mainRule);

        return output.toString();
    }

    private void writeRule(Appendable output, String name, Expression expression) {
        writeName(output, name);
        writeSpace(output);
        writeToken(output, "=");
        writeSpace(output);
        writeExpression(output, expression);
        writeLineBreak(output);
    }

    private void writeExpression(Appendable output, Expression expression) {
        if (expression instanceof Wrapping) {
            writeWrapping(output, (Wrapping)expression);
        }
        else if (expression instanceof Alternation) {
            writeAlternation(output, (Alternation)expression);
        }
        else if (expression instanceof Option) {
            writeOption(output, (Option)expression);
        }
        else if (expression instanceof Reference) {
            writeReference(output, (Reference)expression);
        }
        else if (expression instanceof Repeat) {
            writeRepeat(output, (Repeat)expression);
        }
        else if (expression instanceof Sequence) {
            writeSequence(output, (Sequence)expression);
        }
        else if (expression instanceof Literal) {
            writeLiteral(output, (Literal)expression);
        }
        else if (expression instanceof Wildcard) {
            writeWildcard(output, (Wildcard)expression);
        }
        else {
            throw ErrorFactory.internalError("not implemented expression: " + expression);
        }
    }

    private void writeWrapping(Appendable output, Wrapping wrapping) {
        writeToken(output, "<");
        writeName(output, wrapping.type.name().toLowerCase());

        if (wrapping.argument != null) {
            writeToken(output, " ");
            writeName(output, wrapping.argument);
        }

        writeToken(output, ":");
        writeSpace(output);
        writeExpression(output, wrapping.content);
        writeToken(output, ">");
    }

    private void writeAlternation(Appendable output, Alternation alternation) {
        for (var i = 0; i < alternation.items.size(); i++) {
            if (i > 0) {
                writeSpace(output);
                writeToken(output, "|");
                writeSpace(output);
            }

            writeExpression(output, alternation.items.get(i));
        }
    }

    private void writeOption(Appendable output, Option option) {
        writeToken(output, "[");
        writeExpression(output, option.content);
        writeToken(output, "]");
    }

    private void writeReference(Appendable output, Reference reference) {
        writeName(output, reference.name);
    }

    private void writeRepeat(Appendable output, Repeat repeat) {
        writeToken(output, "{+");
        writeExpression(output, repeat.content);
        writeToken(output, "}");
    }

    private void writeSequence(Appendable output, Sequence sequence) {
        for (var i = 0; i < sequence.items.size(); i++) {
            if (i > 0) {
                writeToken(output, " ");
            }

            writeExpression(output, sequence.items.get(i));
        }
    }

    private void writeLiteral(Appendable output, Literal literal) {
        if (literal.symbol instanceof SymbolChar symbol) {
            writeToken(output, "\"");
            writeToken(output, String.valueOf(symbol.value));
            writeToken(output, "\"");
        }
        else if (literal.symbol instanceof SymbolRange symbol) {
            writeToken(output, "'");
            writeToken(output, String.valueOf(symbol.begin));
            writeToken(output, "-");
            writeToken(output, String.valueOf(symbol.end));
            writeToken(output, "'");
        }
        else {
            throw ErrorFactory.notImplemented();
        }
    }

    private void writeWildcard(Appendable output, Wildcard wildcard) {
        writeToken(output, "*".repeat(wildcard.level));
    }

    private void writeLineBreak(Appendable output) {
        try {
            output.append('\n');
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeToken(Appendable output, String token) {
        try {
            output.append(token);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeSpace(Appendable output) {
        try {
            output.append(' ');
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeName(Appendable output, String name) {
        try {
            output.append(name);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
