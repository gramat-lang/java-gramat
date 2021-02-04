package org.gramat.util;

import org.gramat.expressions.Expression;
import org.gramat.runtime.RExpression;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ExpressionList extends AbstractList<Expression> {

    public static ExpressionList of(Expression... items) {
        return new ExpressionList(items);
    }

    public static ExpressionList of(List<Expression> items) {
        return new ExpressionList(items.toArray(Expression[]::new));
    }

    public static ExpressionListBuilder builder() {
        return new ExpressionListBuilder();
    }

    public static class ExpressionListBuilder extends ArrayList<Expression> {
        public ExpressionList build() {
            return new ExpressionList(toArray(Expression[]::new));
        }
    }

    private final Expression[] items;

    private ExpressionList(Expression[] items) {
        this.items = items;
    }

    public Expression first() {
        return items[0];
    }

    public ExpressionList map(UnaryOperator<Expression> mapper) {
        var copy = new Expression[items.length];

        for (int i = 0; i < items.length; i++) {
            copy[i] = mapper.apply(items[i]);
        }

        return new ExpressionList(copy);
    }

    public <T> List<T> mapList(Function<Expression, T> mapper) {
        var result = new ArrayList<T>();

        for (Expression item : items) {
            result.add(mapper.apply(item));
        }

        return result;
    }

    public boolean containsOf(Class<? extends Expression> typeClass) {
        for (var item : items) {
            if (typeClass.isInstance(item)) {
                return true;
            }
        }
        return false;
    }

    public RExpression[] build() {
        var result = new RExpression[items.length];

        for (var i = 0; i < items.length; i++) {
            result[i] = items[i].build();
        }

        return result;
    }

    @Override
    public Expression get(int i) {
        return items[i];
    }

    @Override
    public int size() {
        return items.length;
    }

    @Override
    public boolean isEmpty() {
        return items.length == 0;
    }

    @Override
    public Iterator<Expression> iterator() {
        return new Iterator<>() {
            int cursor;

            @Override
            public boolean hasNext() {
                return cursor < items.length;
            }

            @Override
            public Expression next() {
                var i = cursor;
                if (i >= items.length) {
                    throw new NoSuchElementException();
                } else {
                    cursor = i + 1;
                    return items[i];
                }
            }
        };
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(items, items.length, Object[].class);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ExpressionList) {
            return Arrays.equals(items, ((ExpressionList) o).items);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(items);
    }

}
