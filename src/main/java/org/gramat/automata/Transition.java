package org.gramat.automata;

import org.gramat.automata.actions.Action;
import org.gramat.automata.symbols.Symbol;
import org.gramat.automata.tokens.Token;

public class Transition {

    private static final Action[] EMPTY = new Action[0];

    private final State target;
    private final Symbol symbol;
    private final Token token;
    private final Action[] beginActions;
    private final Action[] endActions;

    public Transition(State target, Symbol symbol, Token token, Action[] beginActions, Action[] endActions) {
        this.target = target;
        this.symbol = symbol;
        this.token = token;
        this.beginActions = beginActions != null ? beginActions : EMPTY;
        this.endActions = endActions != null ? endActions : EMPTY;
    }

    public State getTarget() {
        return target;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public Token getToken() {
        return token;
    }

    public Action[] getBeginActions() {
        return beginActions;
    }

    public Action[] getEndActions() {
        return endActions;
    }
}
