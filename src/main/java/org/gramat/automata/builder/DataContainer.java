package org.gramat.automata.builder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataContainer {

    private List<Object> list;
    private Map<String, Object> map;
    private String key;

    public Object buildList(String typeHint) {
        // TODO type hint
        if (map != null) {
            throw new RuntimeException();
        }
        else if (list == null) {
            throw new RuntimeException();
        }
        else {
            return list;
        }
    }

    public Object buildMap(String typeHint) {
        // TODO type hint
        if (list != null) {
            throw new RuntimeException();
        }
        else if (map == null) {
            return null;
        }
        else {
            return map;
        }
    }

    public Object buildValue() {
        if (map != null) {
            throw new RuntimeException();
        }
        else if (list == null  || list.isEmpty()) {
            return null;
        }
        else if (list.size() != 1) {
            throw new RuntimeException();
        }
        else {
            return list.get(0);
        }
    }

    public String buildString() {
        var value = buildValue();

        if (value instanceof String) {
            return (String) value;
        }
        else {
            throw new RuntimeException();
        }
    }

    public void setKey(String key) {
        if (this.key != null) {
            throw new RuntimeException();
        }

        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void add(Object value) {
        if (map != null) {
            throw new RuntimeException();
        }

        if (list == null) {
            list = new ArrayList<>();
        }

        list.add(value);
    }

    public void set(String key, Object value) {
        if (list != null) {
            throw new RuntimeException();
        }

        if (map == null) {
            map = new LinkedHashMap<>();
        }

        if (key == null) {
            throw new RuntimeException();
        }

        // TODO check for duplicated keys

        map.put(key, value);
    }
}
