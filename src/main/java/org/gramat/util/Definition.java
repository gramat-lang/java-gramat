package org.gramat.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Definition {

    private final Class<?> valueClass;
    private final Map<String, Object> attributes;

    public Definition(Class<?> valueClass) {
        this.valueClass = valueClass;
        this.attributes = new HashMap<>();
    }

    public void attr(String name, Object value) {
        attributes.put(name, value);
    }

    public String computeString() {
        var builder = new StringBuilder();

        builder.append(valueClass.getSimpleName());

        builder.append('(');

        var i = 0;

        for (var attr : attributes.entrySet()) {
            if (attr.getValue() != null) {
                if (i > 0) {
                    builder.append(", ");
                }

                builder.append(attr.getKey());
                builder.append('=');
                writeValue(attr.getValue(), builder);
                i++;
            }
        }

        builder.append(')');

        return builder.toString();
    }

    private void writeValue(Object value, StringBuilder out) {
        if (value == null) {
            out.append("null");
        }
        else if (value instanceof Number) {
            out.append(value.toString());
        }
        else if (value instanceof Class) {
            out.append(((Class<?>)value).getSimpleName());
        }
        else if (value instanceof String) {
            writeString((String)value, out);
        }
        else if (value instanceof List) {
            writeList((List<?>)value, out);
        }
        else {
            out.append(value.getClass().getSimpleName())
                    .append("@")
                    .append(Integer.toHexString(value.hashCode()));
        }
    }

    private void writeString(String str, StringBuilder out) {
        out.append('"');

        if (str.length() > 50) {
            str = str.substring(50) + "…";
        }

        // TODO improve escaping
        str = str.replace("\n", "\\n");
        str = str.replace("\"", "\\\"");

        out.append(str);

        out.append('"');
    }

    private void writeList(List<?> list, StringBuilder out) {
        if (list.isEmpty()) {
            out.append("(empty)");
        }
        else if (list.size() == 1) {
            out.append("(1 item)");
        }
        else {
            out.append('(');
            out.append(list.size());
            out.append(" items)");
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Definition) {
            var that = (Definition)other;
            return this.valueClass.equals(that.valueClass)
                    && this.attributes.equals(that.attributes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueClass, attributes.hashCode());
    }
}
