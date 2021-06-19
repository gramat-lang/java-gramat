package org.gramat.automata.builder;

public class ValueBeginInstruction implements DataInstruction {
    private final int beginPosition;

    public ValueBeginInstruction(int beginPosition) {
        this.beginPosition = beginPosition;
    }

    @Override
    public void run(DataContext context) {
        context.pushBeginPosition(beginPosition);
    }
}
