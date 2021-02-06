package org.gramat.expressions.engines;

import org.gramat.exceptions.GramatException;
import org.gramat.expressions.Expression;
import org.gramat.expressions.actions.ListWrapper;
import org.gramat.expressions.actions.NameWrapper;
import org.gramat.expressions.actions.ObjectWrapper;
import org.gramat.expressions.actions.PropertyWrapper;
import org.gramat.expressions.actions.TextWrapper;
import org.gramat.inputs.Location;

public class ActionFactory {
    public static final String LIST_WRAPPER_ID = "list";
    public static final String OBJECT_WRAPPER_ID = "object";
    public static final String TEXT_WRAPPER_ID = "value";
    public static final String PROPERTY_WRAPPER_ID = "set";
    public static final String NAME_WRAPPER_ID = "name";
    public static final String PROPERTY_END_ID = "property-end";
    public static final String NAME_END_ID = "name-end";
    public static final String TEXT_END_ID = "value-end";
    public static final String LIST_END_ID = "list-end";
    public static final String OBJECT_END_ID = "object-end";
    public static final String PROPERTY_BEGIN_ID = "property-begin";
    public static final String NAME_BEGIN_ID = "name-begin";
    public static final String TEXT_BEGIN_ID = "value-begin";
    public static final String LIST_BEGIN_ID = "list-begin";
    public static final String OBJECT_BEGIN_ID = "object-begin";

    public Expression createAction(String id, String keyword, Expression content, Location begin, Location end) {
        if (LIST_WRAPPER_ID.equals(id)) {
            return createList(keyword, content, begin, end);
        }
        else if (OBJECT_WRAPPER_ID.equals(id)) {
            return createObject(keyword, content, begin, end);
        }
        else if (TEXT_WRAPPER_ID.equals(id)) {
            return createText(keyword, content, begin, end);
        }
        else if (PROPERTY_WRAPPER_ID.equals(id)) {
            return createProperty(keyword, content, begin, end);
        }
        else if (NAME_WRAPPER_ID.equals(id)) {
            return createName(keyword, content, begin, end);
        }
        else {
            throw new GramatException("unsupported action: " + id);
        }
    }

    public PropertyWrapper createProperty(String keyword, Expression content, Location begin, Location end) {
        return new PropertyWrapper(begin, end, content, keyword);
    }

    public TextWrapper createText(String keyword, Expression content, Location begin, Location end) {
        return new TextWrapper(begin, end, content, keyword);
    }

    public ObjectWrapper createObject(String keyword, Expression content, Location begin, Location end) {
        return new ObjectWrapper(begin, end, content, keyword);
    }

    public ListWrapper createList(String keyword, Expression content, Location begin, Location end) {
        return new ListWrapper(begin, end, content, keyword);
    }

    public NameWrapper createName(String keyword, Expression content, Location begin, Location end) {
        if (keyword != null) {
            throw new GramatException("Unsupported keyword");
        }
        return new NameWrapper(begin, end, content);
    }
}
