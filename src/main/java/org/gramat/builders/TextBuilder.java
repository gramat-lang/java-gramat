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

    public void setParser(String parser) {
        this.parser = parser;
    }

    public String getParser() {
        return parser;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    @Override
    public void accept(Object value) {
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
