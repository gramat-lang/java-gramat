package org.gramat.expressions;

import org.gramat.actions.Action;
import org.gramat.actions.ActionFactory;
import org.gramat.errors.ErrorFactory;
import org.gramat.location.Location;

import java.util.List;

public class Wrapping extends Expression {

    public final WrappingType type;
    public final String argument;
    public final Expression content;

    Wrapping(Location location, WrappingType type, String argument, Expression content) {
        super(location);
        this.type = type;
        this.argument = argument;
        this.content = content;
    }

    @Override
    public List<Expression> getChildren() {
        return List.of(content);
    }

    public Wrapping derive(Expression newContent) {
        if (content == newContent) {
            return this;
        }
        return new Wrapping(location, type, argument, newContent);
    }

    public Action createBeginAction() {
        switch (type) {
            case KEY: return ActionFactory.keyBegin();
            case LIST: return ActionFactory.listBegin();
            case MAP: return ActionFactory.mapBegin();
            case PUT: return ActionFactory.putBegin();
            case VALUE: return ActionFactory.valueBegin();
            default: throw ErrorFactory.internalError("not implemented type: " + type);
        }
    }

    public Action createEndAction() {
        switch (type) {
            case KEY:
                if (argument != null) {
                    throw ErrorFactory.syntaxError(location, "key does not accept arguments");
                }
                return ActionFactory.keyEnd();
            case LIST: return ActionFactory.listEnd(argument);
            case MAP: return ActionFactory.mapEnd(argument);
            case PUT: return ActionFactory.putEnd(argument);
            case VALUE: return ActionFactory.valueEnd(argument);
            default: throw ErrorFactory.internalError("not implemented type: " + type);
        }
    }
}
