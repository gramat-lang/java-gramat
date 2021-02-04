package org.gramat.util;

import org.gramat.exceptions.GramatException;

import java.util.LinkedHashMap;

public class NamedMap<T> extends LinkedHashMap<String, T> {

    public void set(String name, T value) {
        if (containsKey(name)) {
            throw new GramatException("already defined: " + name);
        }
        put(name, value);
    }

    public T find(String name) {
        var value = get(name);
        if (value == null) {
            throw new GramatException("not found: " + name);
        }
        return value;
    }

}
