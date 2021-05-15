package org.gramat.tools;

import org.gramat.errors.ErrorFactory;

import java.util.LinkedHashMap;

public class NamedMap<T> extends LinkedHashMap<String, T> {

    public void set(String key, T value) {
        if (containsKey(key)) {
            throw ErrorFactory.keyAlreadyExists(key);
        }

        put(key, value);
    }

}
