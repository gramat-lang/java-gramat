package org.gramat.expressions;

import org.gramat.location.Location;
import org.gramat.symbols.SymbolFactory;

import java.util.List;

public class ExpressionFactory {

    public static Wrapping wrapping(Location location, WrappingType type, String argument, Expression content) {
        return new Wrapping(location, type, argument, content);
    }

    public static Reference reference(Location location, String name) {
        return new Reference(location, name);
    }

    public static Sequence sequence(Location location, List<? extends Expression> items) {
        return new Sequence(location, seal(items));
    }

    public static Alternation alternation(Location location, List<? extends Expression> items) {
        return new Alternation(location, seal(items));
    }

    public static Wildcard wildcard(Location location, int level) {
        return new Wildcard(location, level);
    }

    public static Option option(Location location, Expression content) {
        return new Option(location, content);
    }

    public static Repeat repeat(Location location, Expression content, Expression separator) {
        return new Repeat(location, content, separator);
    }

    public static Literal literal(Location location, char value) {
        return new Literal(location, SymbolFactory.character(value));
    }

    public static Literal literal(Location location, char begin, char end) {
        return new Literal(location, SymbolFactory.range(begin, end));
    }

    private static List<Expression> seal(List<? extends Expression> items) {
        return List.of(items.toArray(new Expression[0]));
    }

    private ExpressionFactory() {}
}
