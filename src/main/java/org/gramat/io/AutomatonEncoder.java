package org.gramat.io;

import org.gramat.automata.Automaton;
import org.gramat.automata.State;
import org.gramat.automata.Transition;
import org.gramat.automata.actions.Action;
import org.gramat.automata.symbols.Symbol;
import org.gramat.automata.symbols.SymbolChar;
import org.gramat.automata.symbols.SymbolRange;
import org.gramat.automata.tokens.Token;
import org.gramat.automata.tokens.TokenEmpty;
import org.gramat.automata.tokens.TokenString;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AutomatonEncoder {

    private static final String HEADER = "GA";
    private static final String VERSION = "0.0.1";

    private static final char SEPARATOR = ' ';
    private static final char BREAK = '\n';

    private static final String BEGIN_STATE_ARRAY = "BS";
    private static final String END_STATE_ARRAY = "ES";
    private static final String CREATE_STATE_ITEM = "NS";
    private static final String BEGIN_TRANSITION_ITEM = "NS";
    private static final String END_TRANSITION_ITEM = "NS";
    private static final String BEGIN_SYMBOL_ARRAY = "";
    private static final String END_SYMBOL_ARRAY = "";
    private static final String CREATE_SYMBOL_CHAR_ITEM = "";
    private static final String CREATE_SYMBOL_RANGE_ITEM = "";
    private static final String BEGIN_TRANSITION_ARRAY = "";
    private static final String END_TRANSITION_ARRAY = "";
    private static final String BEGIN_BEFORE_ARRAY = "";
    private static final String ADD_ACTION_ITEM = "";
    private static final String END_BEFORE_ARRAY = "";
    private static final String BEGIN_AFTER_ARRAY = "";
    private static final String END_AFTER_ARRAY = "";
    private static final String BEGIN_TOKEN_ARRAY = "";
    private static final String END_TOKEN_ARRAY = "";
    private static final String DEFINE_TOKEN_STRING_ITEM = "";
    private static final String DEFINE_TOKEN_EMPTY_ITEM = "";
    private static final String BEGIN_ACTION_ARRAY = "";
    private static final String END_ACTION_ARRAY = "";
    private static final String CREATE_ACTION_ITEM = "";
    private static final String SET_INITIAL_STATE = "";

    public static void encode(Appendable output, Automaton automaton) {
        try {
            new AutomatonEncoder(output, automaton).writeAutomaton();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final Appendable output;
    private final Automaton automaton;
    private final Set<State> visitedStates;
    private final Set<Symbol> visitedSymbols;
    private final Set<Token> visitedTokens;
    private final Set<Action> visitedActions;

    private AutomatonEncoder(Appendable output, Automaton automaton) {
        this.output = output;
        this.automaton = automaton;
        this.visitedStates = new HashSet<>();
        this.visitedSymbols = new HashSet<>();
        this.visitedTokens = new HashSet<>();
        this.visitedActions = new HashSet<>();
    }

    private void writeAutomaton() throws IOException {
        write(HEADER, VERSION);
        writeStates();
        writeSymbols();
        writeTokens();
        writeActions();
        writeTransitions();
        writeInitial();
    }

    private void writeStates() throws IOException {
        var states = automaton.getStates();

        write(BEGIN_STATE_ARRAY, states.length);

        for (var stateIndex = 0; stateIndex < states.length; stateIndex++) {
            var state = states[stateIndex];

            writeState(stateIndex, state);
        }

        write(END_STATE_ARRAY);
    }

    private void writeState(int stateIndex, State state) throws IOException {
        write(CREATE_STATE_ITEM, stateIndex, state.isAccepted());
    }

    private void writeSymbols() throws IOException {
        var symbols = automaton.getSymbols();

        write(BEGIN_SYMBOL_ARRAY, symbols.length);

        for (var symbolIndex = 0; symbolIndex < symbols.length; symbolIndex++) {
            var symbol = symbols[symbolIndex];

            writeSymbol(symbolIndex, symbol);
        }

        write(END_SYMBOL_ARRAY);
    }

    private void writeSymbol(int symbolIndex, Symbol symbol) throws IOException {
        if (symbol instanceof SymbolChar sc) {
            writeSymbolChar(symbolIndex, sc);
        }
        else if (symbol instanceof SymbolRange sr) {
            writeSymbolRange(symbolIndex, sr);
        }
        else {
            throw new RuntimeException();
        }
    }

    private void writeSymbolChar(int symbolIndex, SymbolChar symbol) throws IOException {
        write(CREATE_SYMBOL_CHAR_ITEM, symbolIndex);
    }

    private void writeSymbolRange(int symbolIndex, SymbolRange symbol) throws IOException {
        write(CREATE_SYMBOL_RANGE_ITEM, symbolIndex);
    }

    private void writeTokens() throws IOException {
        var tokens = automaton.getTokens();

        write(BEGIN_TOKEN_ARRAY, tokens.length);

        for (var tokenIndex = 0; tokenIndex < tokens.length; tokenIndex++) {
            var token = tokens[tokenIndex];

            writeToken(tokenIndex, token);
        }

        write(END_TOKEN_ARRAY);
    }

    private void writeToken(int tokenIndex, Token token) throws IOException {
        if (token instanceof TokenString ts) {
            writeTokenString(tokenIndex, ts);
        }
        else if (token instanceof TokenEmpty te) {
            writeTokenEmpty(tokenIndex, te);
        }
        else {
            throw new RuntimeException();
        }
    }

    private void writeTokenString(int tokenIndex, TokenString token) throws IOException {
        write(DEFINE_TOKEN_STRING_ITEM, tokenIndex);
    }

    private void writeTokenEmpty(int tokenIndex, TokenEmpty token) throws IOException {
        write(DEFINE_TOKEN_EMPTY_ITEM, tokenIndex);
    }

    private void writeActions() throws IOException {
        var actions = automaton.getActions();

        write(BEGIN_ACTION_ARRAY, actions.length);

        for (var actionIndex = 0; actionIndex < actions.length; actionIndex++) {
            var action = actions[actionIndex];

            writeAction(actionIndex, action);
        }

        write(END_ACTION_ARRAY);
    }

    private void writeAction(int actionIndex, Action action) throws IOException {
        write(CREATE_ACTION_ITEM, actionIndex);
    }

    private void writeTransitions() throws IOException {
        var states = automaton.getStates();

        for (var sourceIndex = 0; sourceIndex < states.length; sourceIndex++) {
            var state = states[sourceIndex];
            var transitions = state.getTransitions();

            write(BEGIN_TRANSITION_ARRAY, transitions.length);

            for (var transition : transitions) {
                writeTransition(transition);
            }

            write(END_TRANSITION_ARRAY, sourceIndex);
        }
    }

    private void writeTransition(Transition transition) throws IOException {
        var targetIndex = automaton.findIndex(transition.getTarget());
        var symbolIndex = automaton.findIndex(transition.getSymbol());
        var tokenIndex = automaton.findIndex(transition.getToken());

        write(BEGIN_TRANSITION_ITEM, targetIndex, symbolIndex, tokenIndex);
        writeActionArray(BEGIN_BEFORE_ARRAY, transition.getBeginActions(), END_BEFORE_ARRAY);
        writeActionArray(BEGIN_AFTER_ARRAY, transition.getEndActions(), END_AFTER_ARRAY);
        write(END_TRANSITION_ITEM);
    }

    private void writeActionArray(String beginKeyword, Action[] actions, String endKeyword) throws IOException {
        write(beginKeyword, actions.length);
        for (var action : actions) {
            var actionIndex = automaton.findIndex(action);

            write(ADD_ACTION_ITEM, actionIndex);
        }
        write(endKeyword);
    }

    private void writeInitial() throws IOException {
        var initial = automaton.getInitial();

        write(SET_INITIAL_STATE, automaton.findIndex(initial));
    }

    private void write(String key, Object... values) throws IOException {
        output.append(key);

        for (var i = 0; i < values.length; i++) {
            if (i > 0) {
                output.append(SEPARATOR);
            }

            writeValue(values[i]);
        }

        output.append(BREAK);
    }

    private void writeValue(Object value) throws IOException {
        var str = value.toString();

        // TODO add escaping

        output.append(str);
    }

}
