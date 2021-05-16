package org.gramat.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataUtils {

    public static <T> List<T> immutableCopy(List<? extends T> data) {
        return new ImmutableList.Builder<T>()
                .addAll(data)
                .build();
    }

    public static <T> Set<T> immutableCopy(Set<? extends T> data) {
        return new ImmutableSet.Builder<T>()
                .addAll(data)
                .build();
    }

    public static <K, V> Map<K, V> immutableCopy(Map<K, V> data) {
        return new ImmutableMap.Builder<K ,V>()
                .putAll(data)
                .build();
    }

    public static <T> Set<T> mutableCopy(Set<T> data) {
        if (data == null) {
            return new LinkedHashSet<>();
        }
        return new LinkedHashSet<>(data);
    }

    public static <T> Set<T> join(Set<T> set1, Set<T> set2) {
        if (set1 != null && set2 != null) {
            var result = new LinkedHashSet<>(set1);
            result.addAll(set2);
            return result;
        }
        else if (set1 != null) {
            return set1;
        }
        else if (set2 != null) {
            return set2;
        }
        return Set.of();
    }

    public static <T> List<T> copy(List<T> items) {
        return new ArrayList<>(items);
    }

    private DataUtils() {}
}
