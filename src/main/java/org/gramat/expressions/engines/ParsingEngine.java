package org.gramat.expressions.engines;

import org.gramat.actions.design.ActionScheme;
import org.gramat.exceptions.SyntaxException;
import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionProgram;
import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Repetition;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.literals.LiteralChar;
import org.gramat.expressions.literals.LiteralRange;
import org.gramat.expressions.literals.LiteralString;
import org.gramat.expressions.misc.ActionExpression;
import org.gramat.expressions.misc.Nop;
import org.gramat.expressions.misc.Reference;
import org.gramat.expressions.misc.Wild;
import org.gramat.inputs.Input;
import org.gramat.util.ExpressionList;
import org.gramat.util.ExpressionMap;

public class ParsingEngine {

    public static ExpressionProgram run(Input input, String mainName) {
        return new ParsingEngine(input).run(mainName);
    }

    private final Input input;
    private final ExpressionMap rules;

    private ParsingEngine(Input input) {
        this.input = input;
        this.rules = new ExpressionMap();
    }

    private ExpressionProgram run(String mainName) {
        readAll();

        var main = rules.find(mainName);

        return new ExpressionProgram(main, rules);
    }

    private void readAll() {
        while (input.alive()) {
            skipBlockVoid(input);

            var nameBegin = input.getLocation();

            var name = tryReadKeyword(input);

            if (name != null) {
                var nameEnd = input.getLocation();

                skipBlockVoid(input);

                var defBegin = input.getLocation();

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

        var location = input.getLocation();
        var expression = readAlternationOrNull(input, multiline);
        if (expression == null) {
            return new Nop(location, location);
        }

        return expression;
    }

    private Expression readAlternationOrNull(Input input, boolean multiline) {
        var items = ExpressionList.builder();

        skipVoid(input, multiline);

        var begin = input.getLocation();
        var end = begin;
        do {
            var item = readSequenceOrNull(input, multiline);
            if (item == null) {
                break;
            }

            items.add(item);

            end = input.getLocation();

            skipVoid(input, multiline);
        } while (tryPull(input, '|'));

        if (items.isEmpty()) {
            return null;
        }
        else if (items.size() == 1) {
            return items.get(0);
        }
        return new Alternation(begin, end, items.build());
    }

    private Expression readSequenceOrNull(Input input, boolean multiline) {
        var items = ExpressionList.builder();

        skipVoid(input, multiline);

        var begin = input.getLocation();
        var end = begin;
        while (input.alive()) {
            var item = readExpressionItemOrNull(input, multiline);
            if (item == null) {
                break;
            }

            items.add(item);

            end = input.getLocation();

            skipVoid(input, multiline);
        }

        if (items.isEmpty()) {
            return null;
        }
        else if (items.size() == 1) {
            return items.get(0);
        }
        return new Sequence(begin, end, items.build());
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
        else if (input.peek() == '<') {
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
        var location = input.getLocation();

        expect(input, '*');

        return new Wild(location, location);
    }

    private Expression readLiteral(Input input) {
        var begin = input.getLocation();
        var buffer = new StringBuilder();

        expect(input, '"');

        while (input.alive() && input.peek() != '"') {
            var c = readCharItem(input);

            buffer.append(c);
        }

        expect(input, '"');

        var end = input.getLocation();
        var value = buffer.toString();
        return new LiteralString(begin, end, value);
    }

    private Expression readGroup(Input input) {
        expect(input, '(');

        var expression = readExpression(input, true);

        skipBlockVoid(input);

        expect(input, ')');

        return expression;
    }

    private Expression readRepetition(Input input) {
        var begin = input.getLocation();

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

        var end = input.getLocation();

        if (min == 0 && max == 0) {
            if (separator != null) {
                return new Optional(begin, end,
                        new Sequence(begin, end,
                                content,
                                new Repetition(begin, end,
                                        new Sequence(begin, end, separator, content)
                                )
                        )
                );
            }
            else {
                return new Repetition(begin, end, content);
            }
        }
        else if (min == 1 && max == 0) {
            if (separator != null) {
                return new Sequence(begin, end, content,
                        new Repetition(begin, end,
                                new Sequence(begin, end, separator, content)
                        )
                );
            }
            else {
                return new Sequence(begin, end, content, new Repetition(begin, end, content));
            }
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    private Expression readOptional(Input input) {
        var begin = input.getLocation();

        expect(input, '[');

        var content = readExpression(input, true);

        skipBlockVoid(input);

        expect(input, ']');

        var end = input.getLocation();
        return new Optional(begin, end, content);
    }

    private Expression readChars(Input input) {
        var begin = input.getLocation();
        var items = ExpressionList.builder();

        expect(input, '\'');

        do {
            var c = readLiteralCharOrRange(input);

            items.add(c);
        } while (tryPull(input, ' '));

        expect(input, '\'');

        var end = input.getLocation();
        if (items.size() == 1) {
            return items.get(0);
        }
        return new Alternation(begin, end, items.build());
    }

    private Expression readLiteralCharOrRange(Input input) {
        if (input.peek() == ' ') {
            throw new SyntaxException("cannot start with space", input.getLocation());
        }
        var beginPos = input.getLocation();
        var beginChr = readCharItem(input);

        if (tryPull(input, '-')) {
            var endChr = readCharItem(input);
            var endPos = input.getLocation();
            return new LiteralRange(beginPos, endPos, beginChr, endChr);
        }
        else {
            var endPos = input.getLocation();
            return new LiteralChar(beginPos, endPos, beginChr);
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
                    throw new SyntaxException("unexpected escaped char: " + e, input.getLocation());
            }
        }

        return c;
    }

    private Expression readReference(Input input) {
        var begin = input.getLocation();
        var keyword = readKeyword(input);  // TODO improve
        var end = input.getLocation();
        return new Reference(begin, end, keyword);
    }

    private Expression readAction(Input input) {
        var begin = input.getLocation();

        expect(input, '<');

        var actionSymbol = input.pull();
        var actionScheme = findActionScheme(actionSymbol);

        skipBlockVoid(input);

        String argument = tryReadKeyword(input);

        if (argument != null) {
            skipBlockVoid(input);
        }

        var content = readGroup(input);

        skipInlineVoid(input);

        expect(input, actionSymbol);
        expect(input, '>');

        var end = input.getLocation();

        return new ActionExpression(begin, end, actionScheme, content, argument);
    }

    public static ActionScheme findActionScheme(char chr) {
        switch (chr) {
            case '@': return ActionScheme.CREATE_OBJECT;
            case '#': return ActionScheme.CREATE_LIST;
            case '%': return ActionScheme.CREATE_TEXT;
            case '$': return ActionScheme.SET_METADATA;
            case '=': return ActionScheme.SET_PROPERTY;
            default: throw new RuntimeException();
        }
    }

    public static char findActionChar(ActionScheme scheme) {
        switch (scheme) {
            case CREATE_OBJECT: return '@';
            case CREATE_LIST: return '#';
            case CREATE_TEXT: return '%';
            case SET_METADATA: return '$';
            case SET_PROPERTY: return '=';
            default: throw new RuntimeException();
        }
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
            throw new SyntaxException("expected keyword", input.getLocation());
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

    private void expect(Input input, char expected) {
        var actual = input.pull();

        if (actual != expected) {
            throw new SyntaxException("expected char " + expected, input.getLocation());
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
