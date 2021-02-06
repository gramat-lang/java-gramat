package org.gramat.exceptions.reporting;

import java.util.ArrayList;
import java.util.List;

public class ErrorReport {

    public static ErrorReport begin() {
        return new ErrorReport();
    }

    private final List<ErrorDetail> items;

    private ErrorReport() {
        items = new ArrayList<>();
    }

    public ErrorReport add(String name, Object value) {
        items.add(new ErrorDetailEntry(name, ErrorDetail.of(value)));
        return this;
    }

    public ErrorDetail end() {
        if (items.isEmpty()) {
            return null;
        }
        else if (items.size() == 1) {
            return items.get(0);
        }
        return new ErrorDetailGroup(items.toArray(ErrorDetail[]::new));
    }
}
