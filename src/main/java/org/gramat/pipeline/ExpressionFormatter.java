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

public class ExpressionFormatter {
    public String toString(ExpressionProgram program, String mainName) {
        var output = new StringBuilder();

        writeRule(output, mainName, program.main);

        for (var entry : program.dependencies.entrySet()) {
            writeRule(output, entry.getKey(), entry.getValue());
        }

        return output.toString();
    }

    private void writeRule(StringBuilder output, String name, Expression expression) {
        writeName(output, name);
        writeSpace(output);
        writeToken(output, "=");
        writeSpace(output);
        writeExpression(output, expression);
        writeLineBreak(output);
    }

    private void writeExpression(StringBuilder output, Expression expression) {
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

    private void writeWrapping(StringBuilder output, Wrapping wrapping) {
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

    private void writeAlternation(StringBuilder output, Alternation alternation) {
        for (var i = 0; i < alternation.items.size(); i++) {
            if (i > 0) {
                writeSpace(output);
                writeToken(output, "|");
                writeSpace(output);
            }

            writeExpression(output, alternation.items.get(i));
        }
    }

    private void writeOption(StringBuilder output, Option option) {
        writeToken(output, "[");
        writeExpression(output, option.content);
        writeToken(output, "]");
    }

    private void writeReference(StringBuilder output, Reference reference) {
        writeName(output, reference.name);
    }

    private void writeRepeat(StringBuilder output, Repeat repeat) {
        writeToken(output, "{+");
        writeExpression(output, repeat.content);
        writeToken(output, "}");
    }

    private void writeSequence(StringBuilder output, Sequence sequence) {
        for (var i = 0; i < sequence.items.size(); i++) {
            if (i > 0) {
                writeToken(output, " ");
            }

            writeExpression(output, sequence.items.get(i));
        }
    }

    private void writeLiteral(StringBuilder output, Literal literal) {
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

    private void writeWildcard(StringBuilder output, Wildcard wildcard) {
        writeToken(output, "*".repeat(wildcard.level));
    }

    private void writeLineBreak(StringBuilder output) {
        output.append('\n');
    }

    private void writeToken(StringBuilder output, String token) {
        output.append(token);
    }

    private void writeSpace(StringBuilder output) {
        output.append(' ');
    }

    private void writeName(StringBuilder output, String name) {
        output.append(name);
    }

}
