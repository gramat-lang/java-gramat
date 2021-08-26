package org.gramat.automata.builder;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
public class DataContainer {

    private final List<ValueBase> values;

    private String key;

    public DataContainer() {
        values = new ArrayList<>();
    }

    public Object buildList(String typeHint) {
        var list = new ArrayList<>(); // TODO use type hint

        for (var value : values) {
            if (value instanceof ValueItem item) {
                list.add(item.value());
            }
            else {
                throw new RuntimeException("unsupported value");
            }
        }

        return list;
    }

    public Object buildMap(String typeHint) {
        var map = new LinkedHashMap<String, Object>();  // TODO use type hint

        for (var value : values) {
            if (value instanceof ValuePair pair) {
                if (map.containsKey(pair.key())) {
                    log.warn("overridden key: {}", pair.key());
                }

                map.put(pair.key(), pair.value());
            }
            else {
                throw new RuntimeException("unsupported value: " + value);
            }
        }

        return map;
    }

    public Object buildValue() {
        if (values.isEmpty()) {
            return null;
        }
        else if (values.size() != 1) {
            throw new RuntimeException("too much values");
        }

        var value = values.get(0);
        if (value instanceof ValueItem item) {
            return item.value();
        }
        else {
            throw new RuntimeException("unsupported value: " + value);
        }
    }

    public String buildString() {
        var value = buildValue();

        if (value instanceof String str) {
            return str;
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

    public void addItem(Object value) {
        var item = new ValueItem(value);

        values.add(item);
    }

    public void addPair(String key, Object value) {
        var pair = new ValuePair(key, value);

        values.add(pair);
    }
}
