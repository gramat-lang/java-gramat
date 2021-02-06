package org.gramat.eval;

import org.gramat.builders.Builder;
import org.gramat.builders.ListBuilder;
import org.gramat.builders.MetadataBuilder;
import org.gramat.builders.ObjectBuilder;
import org.gramat.builders.PropertyBuilder;
import org.gramat.builders.RootBuilder;
import org.gramat.builders.TextBuilder;
import org.gramat.exceptions.EvalException;
import org.gramat.exceptions.reporting.ErrorReport;

import java.util.ArrayDeque;
import java.util.Deque;

public class EvalBuilder {

    private final EvalEngine engine;
    private final Deque<Builder> stack;

    public EvalBuilder(EvalEngine engine) {
        this.engine = engine;
        this.stack = new ArrayDeque<>();

        this.stack.push(new RootBuilder());
    }

    public void performPushList(int actionID) {
        stack.push(new ListBuilder());
    }

    public void performPopList(int actionID, String typeHint) {
        var listBuilder = popBuilder(actionID, ListBuilder.class);

        if (typeHint != null) {
            listBuilder.acceptType(typeHint);
        }

        var listValue = listBuilder.build(engine);

        peekBuilder(actionID).acceptContent(listValue);
    }

    public void performPushMetadata(int actionID) {
        stack.push(new MetadataBuilder());
    }

    public void performPopMetadata(int actionID, String metaName) {
        var metadataBuilder = popBuilder(actionID, MetadataBuilder.class);
        var metaValue = metadataBuilder.build(engine);

        var builder = peekBuilder(actionID);

        builder.acceptMetadata(metaName, metaValue);
    }

    public void performPushProperty(int actionID) {
        stack.push(new PropertyBuilder());
    }

    public void performPopProperty(int actionID, String nameHint) {
        var propertyBuilder = popBuilder(actionID, PropertyBuilder.class);
        if (nameHint != null) {
            propertyBuilder.acceptName(nameHint);
        }

        var propertyName = propertyBuilder.getName();
        var propertyValue = propertyBuilder.build(engine);

        var objectBuilder = peekBuilder(actionID, ObjectBuilder.class);

        objectBuilder.setAttribute(propertyName, propertyValue);
    }

    public void performPushObject(int actionID) {
        stack.push(new ObjectBuilder());
    }

    public void performPopObject(int actionID, String typeHint) {
        var objectBuilder = popBuilder(actionID, ObjectBuilder.class);
        if (typeHint != null) {
            objectBuilder.acceptType(typeHint);
        }

        var objectValue = objectBuilder.build(engine);

        peekBuilder(actionID).acceptContent(objectValue);
    }

    public void performPushText(int actionID, int beginPosition) {
        stack.push(new TextBuilder(beginPosition));
    }

    public void performPopText(int actionID, int endPosition, String parserHint) {
        var textBuilder = popBuilder(actionID, TextBuilder.class);
        if (parserHint != null) {
            textBuilder.acceptParser(parserHint);
        }

        textBuilder.setEndPosition(endPosition);

        var textValue = textBuilder.build(engine);

        peekBuilder(actionID).acceptContent(textValue);
    }

    public Object pop() {
        // TODO no action ID available here
        var root = popBuilder(0, RootBuilder.class);

        return root.build(engine);
    }


    private <T extends Builder> T popBuilder(int actionID, Class<T> builderClass) {
        var builder = stack.pop();

        if (!builderClass.isInstance(builder)) {
            throw new EvalException("rejected: wrong builder ", ErrorReport.begin()
                    .add("actual value", builder)
                    .add("expected class", builderClass)
                    .add("current stack", stack)
                    .end(), actionID, null);
        }

        return builderClass.cast(builder);
    }

    private Builder peekBuilder(int actionID) {
        var builder = stack.peek();
        if (builder == null) {
            throw new EvalException("rejected! missing builder", actionID, null);
        }
        return builder;
    }

    private <T extends Builder> T peekBuilder(int actionID, Class<T> builderClass) {
        var builder = peekBuilder(actionID);

        if (!builderClass.isInstance(builder)) {
            throw new EvalException("rejected: wrong builder", actionID, null);
        }

        return builderClass.cast(builder);
    }

}
