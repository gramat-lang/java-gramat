package org.gramat.exceptions.reporting;

import java.io.PrintStream;
import java.util.ArrayList;

public interface ErrorDetail {

    int MAX_ITEMS = 50;

    static ErrorDetail of(Object value) {
        if (value == null) {
            return makeString("null");
        }
        if (value instanceof Class) {
            return makeString((Class<?>)value);
        }
        else if (value instanceof Iterable) {
            return makeGroup((Iterable<?>)value);
        }
        return makeString(value.toString());
    }

    static ErrorDetail makeGroup(Iterable<?> iterable) {
        var items = new ArrayList<ErrorDetail>();

        for (var item : iterable) {
            items.add(of(item));

            if (items.size() >= MAX_ITEMS) {
                // TODO add incomplete mark
                break;
            }
        }

        return new ErrorDetailGroup(items.toArray(ErrorDetail[]::new));
    }

    static ErrorDetail makeString(Class<?> typeClass) {
        return new ErrorDetailString(typeClass.getName());
    }

    static ErrorDetail makeString(String value) {
        return new ErrorDetailString(value);
    }

    default void printDetail(PrintStream out) {
        printDetail(out, 0);
    }

    void printDetail(PrintStream out, int indentation);
}
