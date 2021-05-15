package org.gramat.expressions;

import org.gramat.location.Location;

import java.util.List;

public class Repeat extends Expression {

    public final Expression content;
    public final Expression separator;

    Repeat(Location location, Expression content, Expression separator) {
        super(location);
        this.content = content;
        this.separator = separator;
    }

    @Override
    public List<Expression> getChildren() {
        if (separator != null) {
            return List.of(content, separator);
        }
        return List.of(content);
    }

    public Repeat derive(Expression newContent, Expression newSeparator) {
        if (content == newContent && separator == newSeparator) {
            return this;
        }
        return new Repeat(location, newContent, newSeparator);
    }
}
