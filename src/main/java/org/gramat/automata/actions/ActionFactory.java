package org.gramat.automata.actions;

import org.gramat.automata.tokens.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ActionFactory {

    private final List<Action> actions;

    public ActionFactory() {
        actions = new ArrayList<>();
    }

    public KeyBegin createKeyBegin(int group) {
        return getOrCreate(KeyBegin.class, () -> new KeyBegin(group), item -> item.getGroup() == group);
    }

    public KeyEnd createKeyEnd(int group) {
        return getOrCreate(KeyEnd.class, () -> new KeyEnd(group), item -> item.getGroup() == group);
    }

    public ListBegin createListBegin(int group) {
        return getOrCreate(ListBegin.class, () -> new ListBegin(group), item -> item.getGroup() == group);
    }

    public ListEnd createListEnd(int group, String typeHint) {
        return getOrCreate(ListEnd.class, () -> new ListEnd(group, typeHint),
                item -> item.getGroup() == group && Objects.equals(item.typeHint, typeHint));
    }

    public MapBegin createMapBegin(int group) {
        return getOrCreate(MapBegin.class, () -> new MapBegin(group), item -> item.getGroup() == group);
    }

    public MapEnd createMapEnd(int group, String typeHint) {
        return getOrCreate(MapEnd.class, () -> new MapEnd(group, typeHint),
                item -> item.getGroup() == group && Objects.equals(item.getTypeHint(), typeHint));
    }

    public PutBegin createPutBegin(int group) {
        return getOrCreate(PutBegin.class, () -> new PutBegin(group), item -> item.getGroup() == group);
    }

    public PutEnd createPutEnd(int group, String keyHint) {
        return getOrCreate(PutEnd.class, () -> new PutEnd(group, keyHint),
                item -> item.getGroup() == group && Objects.equals(item.keyHint, keyHint));
    }

    public ValueBegin createValueBegin(int group) {
        return getOrCreate(ValueBegin.class, () -> new ValueBegin(group), item -> item.getGroup() == group);
    }

    public ValueEnd createValueEnd(int group, String typeHint) {
        return getOrCreate(ValueEnd.class, () -> new ValueEnd(group, typeHint),
                item -> item.getGroup() == group && Objects.equals(item.typeHint, typeHint));
    }

    public PushToken createPush(int group, Token token) {
        return getOrCreate(PushToken.class, () -> new PushToken(group, token),
                item -> item.getGroup() == group && item.token.equals(token));
    }

    public PopToken createPop(int group, Token token) {
        return getOrCreate(PopToken.class, () -> new PopToken(group, token),
                item -> item.getGroup() == group && item.token.equals(token));
    }

    private <T extends Action> T getOrCreate(Class<T> type, Supplier<T> creator, Predicate<T> matcher) {
        for (var action : actions) {
            if (type.isInstance(action)) {
                var instance =  type.cast(action);

                if (matcher.test(instance)) {
                    return instance;
                }
            }
        }

        var action = creator.get();

        actions.add(action);

        return action;
    }

    public Action[] toArray() {
        return actions.toArray(new Action[0]);
    }
}
