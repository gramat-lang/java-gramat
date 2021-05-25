package org.gramat.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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

    public static <T> void addAll(Collection<T> target, Iterable<? extends T> items) {
        for (var item : items) {
            target.add(item);
        }
    }

    public static <T> Iterator<T> immutableIterator(Iterable<T> iterable) {
        return new Iterator<>() {
            private final Iterator<T> iterator = iterable.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next();
            }
        };
    }

    public static <T> Iterator<T> iteratorOf(T[] items) {
        return new Iterator<>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < items.length;
            }

            @Override
            public T next() {
                if (i < items.length) {
                    var item = items[i];
                    i++;
                    return item;
                }
                else {
                    throw new NoSuchElementException();
                }
            }
        };
    }

    public static <T> Iterator<T> iteratorOf(T item) {
        return new Iterator<>() {
            boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public T next() {
                if (hasNext) {
                    hasNext = false;
                    return item;
                }
                else {
                    throw new NoSuchElementException();
                }
            }
        };
    }

    private DataUtils() {}
}
