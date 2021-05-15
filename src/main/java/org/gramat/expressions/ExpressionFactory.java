package org.gramat.expressions;

import org.gramat.location.Location;
import org.gramat.symbols.SymbolFactory;
import org.gramat.tools.DataUtils;

import java.util.List;

public class ExpressionFactory {

    private int count;

    public ExpressionFactory() {
        count = 0;
    }

    public Wrapping wrapping(Location location, WrappingType type, String argument, Expression content) {
        count++;
        return new Wrapping(location, type, argument, content);
    }

    public Reference reference(Location location, String name) {
        count++;
        return new Reference(location, name);
    }

    public Sequence sequence(Location location, List<? extends Expression> items) {
        count++;
        return new Sequence(location, DataUtils.immutableCopy(items));
    }

    public Alternation alternation(Location location, List<? extends Expression> items) {
        count++;
        return new Alternation(location, DataUtils.immutableCopy(items));
    }

    public Wildcard wildcard(Location location, int level) {
        count++;
        return new Wildcard(location, level);
    }

    public Option option(Location location, Expression content) {
        count++;
        return new Option(location, content);
    }

    public Repeat repeat(Location location, Expression content, Expression separator) {
        count++;
        return new Repeat(location, content, separator);
    }

    public Literal literal(Location location, char value) {
        count++;
        return new Literal(location, SymbolFactory.character(value));
    }

    public Literal literal(Location location, char begin, char end) {
        count++;
        return new Literal(location, SymbolFactory.range(begin, end));
    }

    public int getCount() {
        return count;
    }
}
