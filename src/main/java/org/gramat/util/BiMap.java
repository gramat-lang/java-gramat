package org.gramat.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BiMap<K1,K2, V> {

    private final Map<K1, Map<K2, V>> map;

    public BiMap() {
        map = new LinkedHashMap<>();
    }

    public V get(K1 key1, K2 key2) {
        var m1 = map.get(key1);
        if (m1 == null) {
            return null;
        }
        return m1.get(key2);
    }

    public V put(K1 key1, K2 key2, V value) {
        var m1 = map.computeIfAbsent(key1, k -> new LinkedHashMap<>());

        return m1.put(key2, value);
    }

    public V computeIfAbsent(K1 key1, K2 key2, Supplier<V> supplier) {
        var m1 = map.computeIfAbsent(key1, k -> new LinkedHashMap<>());
        return m1.computeIfAbsent(key2, k -> supplier.get());
    }

}
