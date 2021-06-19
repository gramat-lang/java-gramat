package org.gramat.automata.actions;

import org.gramat.automata.tokens.TokenFactory;
import org.gramat.machine.operations.Operation;
import org.gramat.machine.operations.OperationType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ActionFactory {

    private final TokenFactory tokens;

    private final List<Action> actions;

    public ActionFactory(TokenFactory tokens) {
        this.tokens = tokens;
        this.actions = new ArrayList<>();
    }

    public Action create(Operation operation) {
        return switch (operation.mode()) {
            case BEGIN -> createBegin(operation.type(), operation.argument());
            case END -> createEnd(operation.type(), operation.argument());
        };
    }

    private Action createBegin(OperationType type, String argument) {
        return switch (type) {
            case KEY -> createKeyBegin();
            case LIST -> createListBegin();
            case MAP -> createMapBegin();
            case PUT -> createPutBegin();
            case VALUE -> createValueBegin();
            case TOKEN -> createBeginToken(argument);
        };
    }

    private KeyBegin createKeyBegin() {
        return getOrCreate(
                KeyBegin.class,
                KeyBegin::new,
                instance -> true);
    }

    private ListBegin createListBegin() {
        return getOrCreate(
                ListBegin.class,
                ListBegin::new,
                instance -> true);
    }

    private MapBegin createMapBegin() {
        return getOrCreate(
                MapBegin.class,
                MapBegin::new,
                instance -> true);
    }

    private PutBegin createPutBegin() {
        return getOrCreate(
                PutBegin.class,
                PutBegin::new,
                instance -> true);
    }

    private ValueBegin createValueBegin() {
        return getOrCreate(
                ValueBegin.class,
                ValueBegin::new,
                instance -> true);
    }

    private PushToken createBeginToken(String argument) {
        var token = tokens.token(argument);

        return getOrCreate(
                PushToken.class,
                () -> new PushToken(token),
                instance -> instance.getToken() == token);
    }


    private Action createEnd(OperationType type, String argument) {
        return switch (type) {
            case KEY -> createKeyEnd();
            case LIST -> createListEnd(argument);
            case MAP -> createMapEnd(argument);
            case PUT -> createPutEnd(argument);
            case VALUE -> createValueEnd(argument);
            case TOKEN -> createPop(argument);
        };
    }

    private KeyEnd createKeyEnd() {
        return getOrCreate(
                KeyEnd.class,
                KeyEnd::new,
                instance -> true);
    }

    private ListEnd createListEnd(String typeHint) {
        return getOrCreate(ListEnd.class,
                () -> new ListEnd(typeHint),
                instance -> Objects.equals(instance.getTypeHint(), typeHint));
    }

    private MapEnd createMapEnd(String typeHint) {
        return getOrCreate(MapEnd.class,
                () -> new MapEnd(typeHint),
                instance -> Objects.equals(instance.getTypeHint(), typeHint));
    }

    private PutEnd createPutEnd(String keyHint) {
        return getOrCreate(PutEnd.class,
                () -> new PutEnd(keyHint),
                instance -> Objects.equals(instance.getKeyHint(), keyHint));
    }

    private ValueEnd createValueEnd(String typeHint) {
        return getOrCreate(ValueEnd.class,
                () -> new ValueEnd(typeHint),
                instance -> Objects.equals(instance.getTypeHint(), typeHint));
    }

    private PopToken createPop(String argument) {
        var token = tokens.token(argument);
        return getOrCreate(PopToken.class,
                () -> new PopToken(token),
                instance -> instance.getToken() == token);
    }


    public Action[] toArray() {
        return actions.toArray(new Action[0]);
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
    }}
