package org.gramat.automata;

import org.gramat.automata.actions.Action;
import org.gramat.automata.symbols.Symbol;
import org.gramat.automata.tokens.Token;

import java.util.Objects;

public class Automaton {

    private final State[] states;
    private final Symbol[] symbols;
    private final Token[] tokens;
    private final Action[] actions;

    private final State initial;

    public Automaton(State[] states, Symbol[] symbols, Token[] tokens, Action[] actions, State initial) {
        this.states = Objects.requireNonNull(states);
        this.symbols = Objects.requireNonNull(symbols);
        this.tokens = Objects.requireNonNull(tokens);
        this.actions = Objects.requireNonNull(actions);
        this.initial = Objects.requireNonNull(initial);
    }

    public State[] getStates() {
        return states;
    }

    public Symbol[] getSymbols() {
        return symbols;
    }

    public Token[] getTokens() {
        return tokens;
    }

    public Action[] getActions() {
        return actions;
    }

    public State getInitial() {
        return initial;
    }

    public int findIndex(State state) {
        return findIndex(state, states);
    }

    public int findIndex(Token token) {
        return findIndex(token, tokens);
    }

    public int findIndex(Symbol symbol) {
        return findIndex(symbol, symbols);
    }

    public int findIndex(Action action) {
        return findIndex(action, actions);
    }

    private <T> int findIndex(T item, T[] array) {
        for (var i = 0; i < array.length; i++) {
            if (array[i] == item) {
                return i;
            }
        }

        throw new RuntimeException();
    }
}
