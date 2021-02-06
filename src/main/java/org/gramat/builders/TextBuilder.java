package org.gramat.builders;

import org.gramat.eval.EvalEngine;
import org.gramat.exceptions.GramatException;
import org.gramat.makers.ValueMaker;

public class TextBuilder implements Builder {
    private final int beginPosition;
    private Integer endPosition;
    private String parser;

    public TextBuilder(int beginPosition) {
        this.beginPosition = beginPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public void acceptParser(String parser) {
        // TODO validate overrides
        this.parser = parser;
    }

    @Override
    public void acceptMetadata(String metaName, Object metaValue) {
        if ("parser".equals(metaName)) {
            acceptParser((String)metaValue); // TODO improve string conversion
        }
        else {
            throw new GramatException("unsupported metadata: " + metaName);
        }
    }

    @Override
    public void acceptContent(Object value) {
        throw new GramatException("error! value cannot accept more values");
    }

    @Override
    public Object build(EvalEngine engine) {
        if (endPosition == null) {
            throw new GramatException("error! missing end position");
        }

        var text = engine.input.segment(beginPosition, endPosition);

        if (parser != null) {
            var maker = engine.makers.find(parser);
            if (!(maker instanceof ValueMaker)) {
                throw new GramatException(parser + " is not a value maker.");
            }
            return ((ValueMaker)maker).make(text);
        }

        return text;
    }
}
