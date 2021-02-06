package org.gramat.actions;

import org.gramat.inputs.Location;

import java.util.Map;
import java.util.Set;

public class ActionTemplate {
    public final String key;
    public final Set<Location> locations;
    public final Map<String, String> attributes;

    public ActionTemplate(String key, Map<String, String> attributes, Set<Location> locations) {
        this.key = key;
        this.attributes = attributes;
        this.locations = locations;
    }

    public Action create(int id) {
        switch (key) {
            case HeapPop.KEY: return HeapPop.create(id, this);
            case HeapPush.KEY: return HeapPush.create(id, this);
            case ListBegin.KEY: return ListBegin.create(id, this);
            case ListEnd.KEY: return ListEnd.create(id, this);
            case NameBegin.KEY: return NameBegin.create(id, this);
            case NameEnd.KEY: return NameEnd.create(id, this);
            case ObjectBegin.KEY: return ObjectBegin.create(id, this);
            case ObjectEnd.KEY: return ObjectEnd.create(id, this);
            case PropertyBegin.KEY: return PropertyBegin.create(id, this);
            case PropertyEnd.KEY: return PropertyEnd.create(id, this);
            case TextBegin.KEY: return TextBegin.create(id, this);
            case TextEnd.KEY: return TextEnd.create(id, this);
            default:
                throw new RuntimeException();
        }
    }
}
