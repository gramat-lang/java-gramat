package org.gramat.builders;

import org.gramat.eval.EvalEngine;
import org.gramat.exceptions.GramatException;
import org.gramat.makers.ObjectMaker;

import java.util.LinkedHashMap;
import java.util.Map;

public class ObjectBuilder implements Builder {

    private String type;
    private Map<String, Object> attributes;

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

    public void acceptType(String type) {
        // TODO validate override
        this.type = type;
    }

    @Override
    public void acceptMetadata(String name, Object value) {
        if ("type".equals(name)) {
            acceptType((String)value); // TODO improve string conversion
        }
        else {
            throw new GramatException("unsupported metadata: " + name);
        }
    }

    @Override
    public void acceptContent(Object value) {
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
