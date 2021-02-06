package org.gramat.builders;

import org.gramat.eval.EvalEngine;
import org.gramat.exceptions.GramatException;
import org.gramat.makers.ObjectMaker;

import java.util.LinkedHashMap;
import java.util.Map;

public class ObjectBuilder implements Builder {

    private String type;
    private Map<String, Object> attributes;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setAttribute(String name, Object value) {
        if (attributes == null) {
            attributes = new LinkedHashMap<>();
        }
        else if (attributes.containsKey(name)) {
            throw new GramatException("rejected! duplicated attribute");
        }

        attributes.put(name, value);
    }

    public Map<String, Object> getAttributes() {
        if (attributes == null) {
            return Map.of();
        }
        return attributes;
    }

    @Override
    public void accept(Object value) {
        throw new GramatException("rejected! objects only accepts properties");
    }

    @Override
    public Object build(EvalEngine engine) {
        var obj = getAttributes();

        if (type != null) {
            var maker = engine.makers.find(type);
            if (!(maker instanceof ObjectMaker)) {
                throw new GramatException(type + " is not a object maker");
            }
            return ((ObjectMaker)maker).make(obj);
        }

        return obj;
    }
}
