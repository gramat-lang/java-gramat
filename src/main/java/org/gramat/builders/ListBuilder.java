package org.gramat.builders;

import org.gramat.eval.EvalEngine;
import org.gramat.exceptions.GramatException;
import org.gramat.makers.ListMaker;

import java.util.ArrayList;
import java.util.List;

public class ListBuilder implements Builder {

    private String type;
    private List<Object> items;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public void accept(Object value) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(value);
    }

    @Override
    public Object build(EvalEngine engine) {
        var list = items != null ? items : List.of();

        if (type != null) {
            var maker = engine.makers.find(type);
            if (!(maker instanceof ListMaker)) {
                throw new GramatException(type + " is not a list maker");
            }
            return ((ListMaker)maker).make(list);
        }

        return list;
    }
}
