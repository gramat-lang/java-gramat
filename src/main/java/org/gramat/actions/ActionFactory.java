package org.gramat.actions;

import org.gramat.errors.ErrorFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionFactory {

    private static final Map<String, ListEnd> listEnds = new HashMap<>();
    private static final Map<String, MapEnd> mapEnds = new HashMap<>();
    private static final Map<String, PutEnd> putEnds = new HashMap<>();
    private static final Map<String, ValueEnd> valueEnds = new HashMap<>();
    private static final List<Ignore> ignores = new ArrayList<>();
    private static final List<Cancel> cancels = new ArrayList<>();
    private static final Map<String, PushToken> pushes = new HashMap<>();
    private static final Map<String, PopToken> pops = new HashMap<>();

    public static KeyBegin keyBegin() {
        return KeyBegin.INSTANCE;
    }

    public static KeyEnd keyEnd(String argument) {
        if (argument != null) {
            throw ErrorFactory.internalError("key does not accept arguments");
        }
        return KeyEnd.INSTANCE;
    }

    public static ListBegin listBegin() {
        return ListBegin.INSTANCE;
    }

    public static ListEnd listEnd(String typeHint) {
        return listEnds.computeIfAbsent(typeHint, ListEnd::new);
    }

    public static MapBegin mapBegin() {
        return MapBegin.INSTANCE;
    }

    public static MapEnd mapEnd(String typeHint) {
        return mapEnds.computeIfAbsent(typeHint, MapEnd::new);
    }

    public static PutBegin putBegin() {
        return PutBegin.INSTANCE;
    }

    public static PutEnd putEnd(String nameHint) {
        return putEnds.computeIfAbsent(nameHint, PutEnd::new);
    }

    public static ValueBegin valueBegin() {
        return ValueBegin.INSTANCE;
    }

    public static ValueEnd valueEnd(String typeHint) {
        return valueEnds.computeIfAbsent(typeHint, ValueEnd::new);
    }

    public static Ignore ignore(Action action) {
        for (var ignore : ignores) {
            if (ignore.action == action) {
                return ignore;
            }
        }

        var ignore = new Ignore(action);

        ignores.add(ignore);

        return ignore;
    }

    public static Cancel cancel(Action action) {
        for (var cancel : cancels) {
            if (cancel.action == action) {
                return cancel;
            }
        }

        var cancel = new Cancel(action);

        cancels.add(cancel);

        return cancel;
    }

    public static PushToken push(String token) {
        return pushes.computeIfAbsent(token, PushToken::new);
    }

    public static PopToken pop(String token) {
        return pops.computeIfAbsent(token, PopToken::new);
    }

    private ActionFactory() {}

}
