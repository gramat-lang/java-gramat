package org.gramat.automating;

import org.gramat.util.Lazy;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StateSet implements Iterable<State> {

    public static StateSet of(State state) {
        return new StateSet(List.of(state));
    }

    public static StateSet of(Collection<State> states) {
        return new StateSet(states);
    }

    public static StateSet of() {
        return new StateSet(List.of());
    }

    private final Set<State> set;
    private final Lazy<String> lazyID;

    private StateSet(Collection<State> states) {
        this.set = new LinkedHashSet<>(states);
        this.lazyID = Lazy.of(() -> set.stream()
                    .sorted(Comparator.comparingInt(a -> a.id))
                    .map(a -> String.valueOf(a.id))
                    .collect(Collectors.joining("_")));

    }

    public String getID() {
        return lazyID.get();
    }

    public boolean add(State state) {
        lazyID.flush();
        return set.add(state);
    }

    @Override
    public Iterator<State> iterator() {
        return set.iterator();
    }

    public boolean contains(State state) {
        return set.contains(state);
    }

    public boolean isPresent() {
        return !set.isEmpty();
    }
}
