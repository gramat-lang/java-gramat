package org.gramat.parsing;

import org.gramat.exceptions.SyntaxException;
import org.gramat.expressions.Expression;
import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Repetition;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.literals.LiteralChar;
import org.gramat.expressions.literals.LiteralRange;
import org.gramat.expressions.literals.LiteralString;
import org.gramat.expressions.misc.Nop;
import org.gramat.expressions.misc.Reference;
import org.gramat.expressions.misc.Wild;
import org.gramat.inputs.Input;
import org.gramat.inputs.Location;
import org.gramat.inputs.Position;
import org.gramat.util.ExpressionList;
import org.gramat.util.ExpressionMap;

import java.util.LinkedHashMap;
import java.util.Map;

public class ParsingEngine {

    private final ExpressionMap rules;
    private final ActionFactory actions;

    public ParsingEngine() {
        this.rules = new ExpressionMap();
        this.actions = new ActionFactory();
    }

    public ExpressionMap getRules() {
        return rules;
    }

    public void readAll(Input input) {
        while (input.alive()) {
            skipBlockVoid(input);

            var nameBegin = input.position();

            var name = tryReadKeyword(input);

            if (name != null) {
                var nameEnd = input.position();

                skipBlockVoid(input);

                var defBegin = input.position();

                if (tryPull(input, '=')) {
                    if (rules.containsKey(name)) {
                        throw new SyntaxException("rule already defined: " + name, nameBegin, nameEnd);
                    }

                    skipBlockVoid(input);

                    var expression = readExpression(input, false);

                    rules.put(name, expression);
                }
                else {
                    throw new SyntaxException("unexpected content", defBegin);
                }
            }
            else {
                break;
            }
        }
    }

    private Expression readExpression(Input input, boolean multiline) {
        skipVoid(input, multiline);

        var position = input.position();
        var expression = readAlternationOrNull(input, multiline);
        if (expression == null) {
            return withLocation(new Nop(), position);
        }

        return expression;
    }

    private Expression readAlternationOrNull(Input input, boolean multiline) {
        var items = ExpressionList.builder();

        skipVoid(input, multiline);

        var begin = input.position();
        var end = begin;
        do {
            var item = readSequenceOrNull(input, multiline);
            if (item == null) {
                break;
            }

            items.add(item);

            end = input.position();

            skipVoid(input, multiline);
        } while (tryPull(input, '|'));

        if (items.isEmpty()) {
            return null;
        }
        else if (items.size() == 1) {
            return items.get(0);
        }
        return withLocation(new Alternation(items.build()), begin, end);
    }

    private Expression readSequenceOrNull(Input input, boolean multiline) {
        var items = ExpressionList.builder();

        skipVoid(input, multiline);

        var begin = input.position();
        var end = begin;
        while (input.alive()) {
            var item = readExpressionItemOrNull(input, multiline);
            if (item == null) {
                break;
            }

            items.add(item);

            end = input.position();

            skipVoid(input, multiline);
        }

        if (items.isEmpty()) {
            return null;
        }
        else if (items.size() == 1) {
            return items.get(0);
        }
        return withLocation(new Sequence(items.build()), begin, end);
    }

    private Expression readExpressionItemOrNull(Input input, boolean multiline) {
        skipVoid(input, multiline);

        if (input.peek() == '"') {
            return readLiteral(input);
        }
        else if (input.peek() == '(') {
            return readGroup(input);
        }
        else if (input.peek() == '{') {
            return readRepetition(input);
        }
        else if (input.peek() == '[') {
            return readOptional(input);
        }
        else if (input.peek() == '@') {
            return readAction(input);
        }
        else if (input.peek() == '\'') {
            return readChars(input);
        }
        else if (input.peek() == '`' || Syntax.isKeywordBegin(input.peek())) {
            return readReference(input);
        }
        else if (input.peek() == '*') {
            return readWild(input);
        }
        else {
            return null;
        }
    }

    private Expression readWild(Input input) {
        expect(input, '*');

        return new Wild();
    }

    private Expression readLiteral(Input input) {
        var begin = input.position();
        var buffer = new StringBuilder();

        expect(input, '"');

        while (input.alive() && input.peek() != '"') {
            var c = readCharItem(input);

            buffer.append(c);
        }

        expect(input, '"');

        var end = input.position();
        var value = buffer.toString();
        return withLocation(new LiteralString(value), begin, end);
    }

    private Expression readGroup(Input input) {
        expect(input, '(');

        var expression = readExpression(input, true);

        skipBlockVoid(input);

        expect(input, ')');

        return expression;
    }

    private Expression readRepetition(Input input) {
        var begin = input.position();

        expect(input, '{');

        var min = 0;
        var max = 0;

        if (tryPull(input, '+')) {
            min = 1;
        }

        Expression content = readExpression(input, true);
        Expression separator;

        skipBlockVoid(input);

        if (tryPull(input, '/')) {
            skipBlockVoid(input);

            separator = readExpression(input, true);
        }
        else {
            separator = null;
        }

        expect(input, '}');

        var end = input.position();
        Expression result;

        if (min == 0 && max == 0) {
            if (separator != null) {
                result = new Optional(new Sequence(ExpressionList.of(content, new Repetition(new Sequence(ExpressionList.of(separator, content))))));
            }
            else {
                result = new Repetition(content);
            }
        }
        else if (min == 1 && max == 0) {
            if (separator != null) {
                result = new Sequence(ExpressionList.of(content, new Repetition(new Sequence(ExpressionList.of(separator, content)))));
            }
            else {
                result = new Sequence(ExpressionList.of(content, new Repetition(content)));
            }
        }
        else {
            throw new UnsupportedOperationException();
        }

         return withLocation(result, begin, end);
    }

    private Expression readOptional(Input input) {
        var begin = input.position();

        expect(input, '[');

        var content = readExpression(input, true);

        skipBlockVoid(input);

        expect(input, ']');

        var end = input.position();
        return withLocation(new Optional(content), begin, end);
    }

    private Expression readChars(Input input) {
        var begin = input.position();
        var items = ExpressionList.builder();

        expect(input, '\'');

        do {
            var c = readLiteralCharOrRange(input);

            items.add(c);
        } while (tryPull(input, ' '));

        expect(input, '\'');

        var end = input.position();
        if (items.size() == 1) {
            return items.get(0);
        }
        return withLocation(new Alternation(items.build()), begin, end);
    }

    private Expression readLiteralCharOrRange(Input input) {
        if (input.peek() == ' ') {
            throw new SyntaxException("cannot start with space", input.position());
        }
        var beginPos = input.position();
        var beginChr = readCharItem(input);

        if (tryPull(input, '-')) {
            var endChr = readCharItem(input);
            return withLocation(new LiteralRange(beginChr, endChr), beginPos, input.position());
        }
        else {
            return withLocation(new LiteralChar(beginChr), beginPos, input.position());
        }
    }

    private char readCharItem(Input input) {
        var c = input.pull();

        if (c == '\\') {
            var e = input.pull();
            switch(e) {
                // Mapped escapes
                case 's': return ' ';
                case 't': return '\t';
                case 'r': return '\r';
                case 'n': return '\n';
                // Direct escapes
                case '\'':
                case '\"':
                case '\\':
                    return e;
                // TODO add more escaped cases
                default:
                    throw new SyntaxException("unexpected escaped char: " + e, input.position());
            }
        }

        return c;
    }

    private Expression readReference(Input input) {
        var begin = input.position();
        var keyword = readKeyword(input);  // TODO improve
        var end = input.position();
        return withLocation(new Reference(keyword), begin, end);
    }

    private Expression readAction(Input input) {
        var begin = input.position();
        var end = input.position();

        expect(input, '@');
        String id = readKeyword(input);
        String name;

        if (tryPull(input, ':')) {
            name = readKeyword(input);
        }
        else {
            name = null;
        }

        skipInlineVoid(input);

        var content = readGroup(input);

        return withLocation(actions.createAction(id, name, content), begin, end);
    }

    private void skipVoid(Input input, boolean multiline) {
        if (multiline) {
            skipBlockVoid(input);
        }
        else {
            skipInlineVoid(input);
        }
    }

    private void skipBlockVoid(Input input) {
        while (Syntax.isBlockVoid(input.peek())) {
            input.pull();
            // TODO add comments
        }
    }

    private void skipInlineVoid(Input input) {
        while (Syntax.isInlineVoid(input.peek())) {
            input.pull();
            // TODO add comments
        }
    }

    private String readKeyword(Input input) {
        var keyword = tryReadKeyword(input);

        if (keyword == null) {
            throw new SyntaxException("expected keyword", input.position());
        }

        return keyword;
    }

    private String tryReadKeyword(Input input) {
        if (tryPull(input, '`')) {
            var keyword = new StringBuilder();

            while (input.alive() && input.peek() != '`') {
                var c = input.pull();

                // TODO add special and escaped chars

                keyword.append(c);
            }

            expect(input, '`');

            return keyword.toString();
        }
        else if (Syntax.isKeywordBegin(input.peek())) {
            var keyword = new StringBuilder();

            do {
                var c = input.pull();

                keyword.append(c);
            }
            while (Syntax.isKeywordContent(input.peek()));

            return keyword.toString();
        }
        else {
            return null;
        }
    }

    private Expression withLocation(Expression expression, Position position) {
        return withLocation(expression, position, position);
    }

    private Expression withLocation(Expression expression, Position begin, Position end) {
        var location = new Location(begin, end);

        expression.locations.add(location);

        return expression;
    }

    private void expect(Input input, char expected) {
        var actual = input.pull();

        if (actual != expected) {
            throw new SyntaxException("expected char " + expected, input.position());
        }
    }

    private boolean tryPull(Input input, char c) {
        if (input.peek() == c) {
            input.pull();
            return true;
        }
        return false;
    }

}
