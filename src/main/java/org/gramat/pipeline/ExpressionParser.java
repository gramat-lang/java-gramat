package org.gramat.pipeline;

import org.gramat.errors.ErrorFactory;
import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionMap;
import org.gramat.expressions.ExpressionRule;
import org.gramat.expressions.ExpressionFactory;
import org.gramat.expressions.WrappingType;
import org.gramat.tools.CharInput;

import java.util.ArrayList;
import java.util.Optional;

public class ExpressionParser {

    public ExpressionMap parseFile(CharInput input) {
        var rules = parseRules(input);

        skipBlockVoid(input);

        if (input.isAlive()) {
            throw ErrorFactory.syntaxError(input.getLocation(),
                    "Unexpected char: %s", input.peek());
        }

        return rules;
    }

    private ExpressionMap parseRules(CharInput input) {
        var rules = new ArrayList<ExpressionRule>();

        while (input.isAlive()) {
            skipBlockVoid(input);

            var ruleOp = parseRule(input);
            if (ruleOp.isEmpty()) {
                break;
            }

            rules.add(ruleOp.get());
        }

        return ExpressionMap.of(rules);
    }

    private void skipInlineVoid(CharInput input) {
        while (input.isAlive()) {
            if (isInlineVoidSymbol(input.peek())) {
                input.move();
            }
            else if (input.pull("/*")) {
                while (!input.pull("*/")) {
                    input.move();
                }
            }
            else {
                break;
            }
        }
    }

    private void skipBlockVoid(CharInput input) {
        while (input.isAlive()) {
            if (isBlockVoidSymbol(input.peek())) {
                input.move();
            }
            else if (input.pull("/*")) {
                while (!input.pull("*/")) {
                    input.move();
                }
            }
            else if (input.pull("//")) {
                while (!input.pull('\n')) {
                    input.move();
                }
            }
            else {
                break;
            }
        }
    }

    private Optional<ExpressionRule> parseRule(CharInput input) {
        var nameOp = parseName(input);

        if (nameOp.isEmpty()) {
            return Optional.empty();
        }

        skipInlineVoid(input);

        expect(input, '=');

        var location = input.beginLocation();

        skipInlineVoid(input);

        var exprOp = parseExpression(input, false);

        if (exprOp.isEmpty()) {
            throw ErrorFactory.syntaxError(location.build(),
                    "Expression expected");
        }

        return Optional.of(new ExpressionRule(nameOp.get(), exprOp.get()));
    }

    private void expect(CharInput input, char symbol) {
        var location = input.beginLocation();
        if (input.peek() != symbol) {
            throw ErrorFactory.syntaxError(location.build(),
                    "SymbolChar %s expected", symbol);
        }

        input.move();
    }

    private Optional<String> parseName(CharInput input) {
        var name = new StringBuilder();

        if (isNameBeginSymbol(input.peek())) {
            do {
                var symbol = input.pull();

                name.append(symbol);
            } while (isNameSymbol(input.peek()));
        }

        if (name.length() == 0) {
            return Optional.empty();
        }

        return Optional.of(name.toString());
    }

    private boolean isNameSymbol(char symbol) {
        return (symbol >= 'a' && symbol <= 'z')
                || (symbol >= 'A' && symbol <= 'Z')
                || (symbol >= '0' && symbol <= '9')
                || (symbol == '_')
                || (symbol == '-');
    }

    private boolean isNameBeginSymbol(char symbol) {
        return (symbol >= 'a' && symbol <= 'z')
                || (symbol >= 'A' && symbol <= 'Z')
                || (symbol == '_');
    }

    private boolean isInlineVoidSymbol(char symbol) {
        return symbol == ' ' || symbol == '\t';
    }

    private boolean isBlockVoidSymbol(char symbol) {
        return symbol == ' ' || symbol == '\t' || symbol == '\r' || symbol == '\n';
    }

    private Optional<Expression> parseExpression(CharInput input, boolean blockMode) {
        var location = input.beginLocation();

        var firstOp = parseSequence(input, blockMode);
        if (firstOp.isEmpty()) {
            return Optional.empty();
        }

        var sequences = new ArrayList<Expression>();

        sequences.add(firstOp.get());

        skipInlineVoid(input);

        while (input.pull('|')) {
            skipInlineVoid(input);

            var sequenceOp = parseSequence(input, blockMode);
            if (sequenceOp.isEmpty()) {
                throw ErrorFactory.syntaxError(input.getLocation(),
                        "Alternation option expression expected");
            }

            sequences.add(sequenceOp.get());
        }

        if (sequences.isEmpty()) {
            return Optional.empty();
        }
        else if (sequences.size() == 1) {
            return Optional.of(sequences.get(0));
        }

        input.endLocation(location);

        var alternation = ExpressionFactory.alternation(location.build(), sequences);
        return Optional.of(alternation);
    }

    private Optional<Expression> parseSequence(CharInput input, boolean blockMode) {
        var items = new ArrayList<Expression>();
        var location = input.beginLocation();

        while (input.isAlive()) {
            var itemOp = parseExpressionItem(input);

            if (itemOp.isEmpty()) {
                break;
            }

            if (blockMode) {
                skipBlockVoid(input);
            }
            else {
                skipInlineVoid(input);
            }

            items.add(itemOp.get());
        }

        if (items.isEmpty()) {
            return Optional.empty();
        }
        else if (items.size() == 1) {
            return Optional.of(items.get(0));
        }

        input.endLocation(location);

        var sequence = ExpressionFactory.sequence(location.build(), items);
        return Optional.of(sequence);
    }

    private Optional<Expression> parseExpressionItem(CharInput input) {
        var symbol = input.peek();

        switch (symbol) {
            case '*': return parseWildcard(input);
            case '(': return parseGroup(input);
            case '<': return parseWrapping(input);
            case '[': return parseOptional(input);
            case '{': return parseRepetition(input);
            case '\"': return parseLiteral(input);
            case '\'': return parseCharClass(input);
            default:
                if (isNameBeginSymbol(symbol)) {
                    return parseReference(input);
                }
                return Optional.empty();
        }
    }

    private Optional<Expression> parseReference(CharInput input) {
        var location = input.beginLocation();
        var nameOp = parseName(input);

        if (nameOp.isEmpty()) {
            return Optional.empty();
        }

        input.endLocation(location);

        var reference = ExpressionFactory.reference(location.build(), nameOp.get());
        return Optional.of(reference);
    }

    private Optional<Expression> parseCharClass(CharInput input) {
        var location = input.beginLocation();
        var options = new ArrayList<Expression>();

        expect(input, '\'');

        do {
            var option = parseCharClassOption(input);

            options.add(option);
        } while (input.pull(' '));

        expect(input, '\'');

        if (options.isEmpty()) {
            return Optional.empty();
        }
        else if (options.size() == 1) {
            return Optional.of(options.get(0));
        }

        var alternation = ExpressionFactory.alternation(location.build(), options);
        return Optional.of(alternation);
    }

    private Expression parseCharClassOption(CharInput input) {
        var location = input.beginLocation();

        if (input.peek() == ' ') {
            throw ErrorFactory.syntaxError(location.build(),
                    "Invalid space position, try with '\\s' instead");
        }

        var symbol = parseStringChar(input);

        if (!input.pull('-')) {
            input.endLocation(location);
            return ExpressionFactory.literal(location.build(), symbol);
        }
        else if (input.peek() == ' ') {
            throw ErrorFactory.syntaxError(location.build(),
                    "Invalid space position, try with '\\s' instead");
        }

        var end = parseStringChar(input);

        input.endLocation(location);

        return ExpressionFactory.literal(location.build(), symbol, end);
    }

    private char parseStringChar(CharInput input) {
        if (!input.isAlive()) {
            throw ErrorFactory.syntaxError(
                    input.getLocation(), "String char expected");
        }

        var location = input.getLocation();
        var symbol = input.pull();

        if (symbol != '\\') {
            return symbol;
        }
        else if (!input.isAlive()) {
            throw ErrorFactory.syntaxError(
                    location, "Escaped char expected");
        }

        symbol = input.pull();

        switch (symbol) {
            case 's': return ' ';
            case 'r': return '\r';
            case 'n': return '\n';
            case 't': return '\t';
            case '\\': return '\\';
            case '\'': return '\'';
            case '\"': return '\"';
            default:
                throw ErrorFactory.syntaxError(
                        location, "Invalid escape sequence: %s", symbol);
        }
    }

    private Optional<Expression> parseLiteral(CharInput input) {
        var location = input.beginLocation();
        var content = new ArrayList<Expression>();

        expect(input, '\"');

        while (input.peek() != '\"') {
            var symbolLocation = input.beginLocation();
            var symbol = parseStringChar(input);

            input.endLocation(symbolLocation);

            var symbolExpression = ExpressionFactory.literal(symbolLocation.build(), symbol);

            content.add(symbolExpression);
        }

        expect(input, '\"');

        if (content.isEmpty()) {
            throw ErrorFactory.syntaxError(input.getLocation(),
                    "String content expected");
        }

        input.endLocation(location);

        var sequence = ExpressionFactory.sequence(location.build(), content);
        return Optional.of(sequence);
    }

    private Optional<Expression> parseRepetition(CharInput input) {
        var location = input.beginLocation();

        expect(input, '{');

        var oneOrMore = input.pull('+');

        skipBlockVoid(input);

        var expressionOp = parseExpression(input, true);

        if (expressionOp.isEmpty()) {
            throw ErrorFactory.syntaxError(input.getLocation(),
                    "Repetition content expression expected");
        }

        skipInlineVoid(input);

        Optional<Expression> separator;

        if (input.pull('/')) {
            skipInlineVoid(input);

            separator = parseExpression(input, true);

            if (separator.isEmpty()) {
                throw ErrorFactory.syntaxError(input.getLocation(),
                        "Separator expression expected");
            }
        }
        else {
            separator = Optional.empty();
        }

        skipBlockVoid(input);

        expect(input, '}');

        input.endLocation(location);

        var repeat = ExpressionFactory.repeat(location.build(), expressionOp.get(), separator.orElse(null));
        if (oneOrMore) {
            return Optional.of(repeat);
        }

        var option = ExpressionFactory.option(location.build(), repeat);
        return Optional.of(option);
    }

    private Optional<Expression> parseOptional(CharInput input) {
        var location = input.beginLocation();

        expect(input, '[');

        skipBlockVoid(input);

        var expressionOp = parseExpression(input, true);

        if (expressionOp.isEmpty()) {
            throw ErrorFactory.syntaxError(input.getLocation(),
                    "Optional content expression expected");
        }

        skipBlockVoid(input);

        expect(input, ']');

        input.endLocation(location);

        var option = ExpressionFactory.option(location.build(), expressionOp.get());
        return Optional.of(option);
    }

    private Optional<Expression> parseWrapping(CharInput input) {
        var location = input.beginLocation();

        expect(input, '<');

        skipBlockVoid(input);

        var rawTypeOp = parseName(input);
        if (rawTypeOp.isEmpty()) {
            throw ErrorFactory.syntaxError(input.getLocation(),
                    "Wrapping type expected");
        }

        var typeOp = WrappingType.parse(rawTypeOp.get());
        if (typeOp.isEmpty()) {
            throw ErrorFactory.syntaxError(input.getLocation(),
                    "Invalid wrapping type: %s", rawTypeOp.get());
        }

        skipInlineVoid(input);

        var argumentOp = parseName(input);
        if (argumentOp.isPresent()) {
            skipInlineVoid(input);
        }

        expect(input, ':');

        skipBlockVoid(input);

        var expressionOp = parseExpression(input, true);

        if (expressionOp.isEmpty()) {
            throw ErrorFactory.syntaxError(input.getLocation(),
                    "Wrapping content expression expected");
        }

        skipBlockVoid(input);

        expect(input, '>');

        input.endLocation(location);

        var wrapping = ExpressionFactory.wrapping(
                location.build(),
                typeOp.get(),
                argumentOp.orElse(null),
                expressionOp.get());
        return Optional.of(wrapping);
    }

    private Optional<Expression> parseGroup(CharInput input) {
        expect(input, '(');

        skipBlockVoid(input);

        var expressionOp = parseExpression(input, true);

        if (expressionOp.isEmpty()) {
            throw ErrorFactory.syntaxError(input.getLocation(),
                    "Group content expression expected");
        }

        skipBlockVoid(input);

        expect(input, ')');

        return expressionOp;
    }

    private Optional<Expression> parseWildcard(CharInput input) {
        var location = input.beginLocation();
        var count = 0;

        while (input.pull('*')) {
            count++;
        }

        if (count == 0) {
            return Optional.empty();
        }

        input.endLocation(location);

        var wildcard = ExpressionFactory.wildcard(location.build(), count);
        return Optional.of(wildcard);
    }

}
