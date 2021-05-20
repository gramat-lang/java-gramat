package org.gramat.pipeline;

import lombok.extern.slf4j.Slf4j;
import org.gramat.errors.ErrorFactory;
import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionFactory;
import org.gramat.data.expressions.ExpressionMap;
import org.gramat.expressions.ExpressionRule;
import org.gramat.expressions.ActionType;
import org.gramat.tools.CharInput;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class ExpressionParser {

    public static ExpressionMap parseFile(CharInput input) {
        return new ExpressionParser(input).parseFile();
    }

    public static Expression parseExpression(CharInput input) {
        return new ExpressionParser(input).parseMain();
    }

    private final CharInput input;
    private final ExpressionFactory factory;

    private ExpressionParser(CharInput input) {
        this.input = input;
        this.factory = new ExpressionFactory();
    }

    private ExpressionMap parseFile() {
        log.debug("Parsing {} file...", Objects.requireNonNullElse(input.getResource(), "expression"));

        var rules = parseRules();

        skipBlockVoid();

        if (input.isAlive()) {
            throw ErrorFactory.syntaxError(input.getLocation(),
                    "Unexpected char: %s", input.peek());
        }

        log.debug("Expression file parsed: {} rule(s), {} item(s)", rules.size(), factory.getCount());

        return rules;
    }

    private Expression parseMain() {
        log.debug("Parsing {} expression...", Objects.requireNonNullElse(input.getResource(), "main"));

        var exprOp = parseExpression(true);

        if (exprOp.isEmpty()) {
            throw ErrorFactory.syntaxError(input.getLocation(),
                    "Expected expression");
        }

        skipBlockVoid();

        if (input.isAlive()) {
            throw ErrorFactory.syntaxError(input.getLocation(),
                    "Unexpected char: %s", input.peek());
        }

        log.debug("Expression parsed: {} item(s)", factory.getCount());

        return exprOp.get();
    }

    private ExpressionMap parseRules() {
        var rules = new ArrayList<ExpressionRule>();

        while (input.isAlive()) {
            skipBlockVoid();

            var ruleOp = parseRule();
            if (ruleOp.isEmpty()) {
                break;
            }

            rules.add(ruleOp.get());
        }

        return ExpressionMap.of(rules);
    }

    private void skipInlineVoid() {
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

    private void skipBlockVoid() {
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

    private Optional<ExpressionRule> parseRule() {
        var nameOp = parseName();

        if (nameOp.isEmpty()) {
            return Optional.empty();
        }

        skipInlineVoid();

        expect('=');

        var location = input.beginLocation();

        skipInlineVoid();

        var exprOp = parseExpression(false);

        if (exprOp.isEmpty()) {
            throw ErrorFactory.syntaxError(location.build(),
                    "Expression expected");
        }

        return Optional.of(new ExpressionRule(nameOp.get(), exprOp.get()));
    }

    private void expect(char symbol) {
        var location = input.beginLocation();
        if (input.peek() != symbol) {
            throw ErrorFactory.syntaxError(location.build(),
                    "SymbolChar %s expected", symbol);
        }

        input.move();
    }

    private Optional<String> parseName() {
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

    private Optional<Expression> parseExpression(boolean blockMode) {
        var location = input.beginLocation();

        var firstOp = parseSequence(blockMode);
        if (firstOp.isEmpty()) {
            return Optional.empty();
        }

        var sequences = new ArrayList<Expression>();

        sequences.add(firstOp.get());

        skipInlineVoid();

        while (input.pull('|')) {
            skipInlineVoid();

            var sequenceOp = parseSequence(blockMode);
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

        var alternation = factory.alternation(location.build(), sequences);
        return Optional.of(alternation);
    }

    private Optional<Expression> parseSequence(boolean blockMode) {
        var items = new ArrayList<Expression>();
        var location = input.beginLocation();

        while (input.isAlive()) {
            var itemOp = parseExpressionItem();

            if (itemOp.isEmpty()) {
                break;
            }

            if (blockMode) {
                skipBlockVoid();
            }
            else {
                skipInlineVoid();
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

        var sequence = factory.sequence(location.build(), items);
        return Optional.of(sequence);
    }

    private Optional<Expression> parseExpressionItem() {
        var symbol = input.peek();

        switch (symbol) {
            case '*': return parseWildcard();
            case '(': return parseGroup();
            case '<': return parseWrapping();
            case '[': return parseOptional();
            case '{': return parseRepetition();
            case '\"', '\'':
                return parseLiteral(symbol);
            case '`': return parseCharClass();
            default:
                if (isNameBeginSymbol(symbol)) {
                    return parseReference();
                }
                return Optional.empty();
        }
    }

    private Optional<Expression> parseReference() {
        var location = input.beginLocation();
        var nameOp = parseName();

        if (nameOp.isEmpty()) {
            return Optional.empty();
        }

        input.endLocation(location);

        var reference = factory.reference(location.build(), nameOp.get());
        return Optional.of(reference);
    }

    private Optional<Expression> parseCharClass() {
        var location = input.beginLocation();
        var options = new ArrayList<Expression>();

        expect('\'');

        do {
            var option = parseCharClassOption();

            options.add(option);
        } while (input.pull(' '));

        expect('\'');

        if (options.isEmpty()) {
            return Optional.empty();
        }
        else if (options.size() == 1) {
            return Optional.of(options.get(0));
        }

        var alternation = factory.alternation(location.build(), options);
        return Optional.of(alternation);
    }

    private Expression parseCharClassOption() {
        var location = input.beginLocation();

        if (input.peek() == ' ') {
            throw ErrorFactory.syntaxError(location.build(),
                    "Invalid space position, try with '\\s' instead");
        }

        var symbol = parseStringChar();

        if (!input.pull('-')) {
            input.endLocation(location);
            return factory.literal(location.build(), symbol);
        }
        else if (input.peek() == ' ') {
            throw ErrorFactory.syntaxError(location.build(),
                    "Invalid space position, try with '\\s' instead");
        }

        var end = parseStringChar();

        input.endLocation(location);

        return factory.literal(location.build(), symbol, end);
    }

    private char parseStringChar() {
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

        return switch (symbol) {
            case 's' -> ' ';
            case 'r' -> '\r';
            case 'n' -> '\n';
            case 't' -> '\t';
            case '\\' -> '\\';
            case '\'' -> '\'';
            case '\"' -> '\"';
            default -> throw ErrorFactory.syntaxError(
                    location, "Invalid escape sequence: %s", symbol);
        };
    }

    private Optional<Expression> parseLiteral(char delimiter) {
        var location = input.beginLocation();
        var content = new ArrayList<Expression>();

        expect(delimiter);

        while (input.peek() != delimiter) {
            var symbolLocation = input.beginLocation();
            var symbol = parseStringChar();

            input.endLocation(symbolLocation);

            var symbolExpression = factory.literal(symbolLocation.build(), symbol);

            content.add(symbolExpression);
        }

        expect(delimiter);

        if (content.isEmpty()) {
            throw ErrorFactory.syntaxError(input.getLocation(),
                    "String content expected");
        }

        input.endLocation(location);

        var sequence = factory.sequence(location.build(), content);
        return Optional.of(sequence);
    }

    private Optional<Expression> parseRepetition() {
        var location = input.beginLocation();

        expect('{');

        var oneOrMore = input.pull('+');

        skipBlockVoid();

        var expressionOp = parseExpression(true);

        if (expressionOp.isEmpty()) {
            throw ErrorFactory.syntaxError(input.getLocation(),
                    "Repetition content expression expected");
        }

        skipInlineVoid();

        Optional<Expression> separator;

        if (input.pull('/')) {
            skipInlineVoid();

            separator = parseExpression(true);

            if (separator.isEmpty()) {
                throw ErrorFactory.syntaxError(input.getLocation(),
                        "Separator expression expected");
            }
        }
        else {
            separator = Optional.empty();
        }

        skipBlockVoid();

        expect('}');

        input.endLocation(location);

        var repeat = factory.repeat(location.build(), expressionOp.get(), separator.orElse(null));
        if (oneOrMore) {
            return Optional.of(repeat);
        }

        var option = factory.option(location.build(), repeat);
        return Optional.of(option);
    }

    private Optional<Expression> parseOptional() {
        var location = input.beginLocation();

        expect('[');

        skipBlockVoid();

        var expressionOp = parseExpression(true);

        if (expressionOp.isEmpty()) {
            throw ErrorFactory.syntaxError(input.getLocation(),
                    "Optional content expression expected");
        }

        skipBlockVoid();

        expect(']');

        input.endLocation(location);

        var option = factory.option(location.build(), expressionOp.get());
        return Optional.of(option);
    }

    private Optional<Expression> parseWrapping() {
        var location = input.beginLocation();

        expect('<');

        skipBlockVoid();

        var rawTypeOp = parseName();
        if (rawTypeOp.isEmpty()) {
            throw ErrorFactory.syntaxError(input.getLocation(),
                    "Wrapping type expected");
        }

        var typeOp = ActionType.parse(rawTypeOp.get());
        if (typeOp.isEmpty()) {
            throw ErrorFactory.syntaxError(input.getLocation(),
                    "Invalid wrapping type: %s", rawTypeOp.get());
        }

        skipInlineVoid();

        var argumentOp = parseName();
        if (argumentOp.isPresent()) {
            skipInlineVoid();
        }

        expect(':');

        skipBlockVoid();

        var expressionOp = parseExpression(true);

        if (expressionOp.isEmpty()) {
            throw ErrorFactory.syntaxError(input.getLocation(),
                    "Wrapping content expression expected");
        }

        skipBlockVoid();

        expect('>');

        input.endLocation(location);

        var wrapping = factory.wrapping(
                location.build(),
                typeOp.get(),
                argumentOp.orElse(null),
                expressionOp.get());
        return Optional.of(wrapping);
    }

    private Optional<Expression> parseGroup() {
        expect('(');

        skipBlockVoid();

        var expressionOp = parseExpression(true);

        if (expressionOp.isEmpty()) {
            throw ErrorFactory.syntaxError(input.getLocation(),
                    "Group content expression expected");
        }

        skipBlockVoid();

        expect(')');

        return expressionOp;
    }

    private Optional<Expression> parseWildcard() {
        var location = input.beginLocation();
        var count = 0;

        while (input.pull('*')) {
            count++;
        }

        if (count == 0) {
            return Optional.empty();
        }

        input.endLocation(location);

        var wildcard = factory.wildcard(location.build(), count);
        return Optional.of(wildcard);
    }

}
