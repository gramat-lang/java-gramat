package org.gramat.pipeline;

import org.gramat.automata.Automaton;
import org.gramat.automata.State;
import org.gramat.automata.StateFactory;
import org.gramat.automata.Transition;
import org.gramat.automata.actions.Action;
import org.gramat.automata.actions.ActionFactory;
import org.gramat.automata.symbols.SymbolChar;
import org.gramat.automata.symbols.SymbolRange;
import org.gramat.machine.operations.Operation;
import org.gramat.automata.symbols.Symbol;
import org.gramat.automata.symbols.SymbolFactory;
import org.gramat.automata.tokens.Token;
import org.gramat.automata.tokens.TokenFactory;
import org.gramat.machine.Machine;
import org.gramat.machine.nodes.Node;
import org.gramat.machine.operations.OperationList;
import org.gramat.machine.operations.OperationType;
import org.gramat.machine.patterns.PatternToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutomatonCompiler {

    public static Automaton run(Machine machine) {
        return new AutomatonCompiler(machine).run();
    }

    private final Machine machine;
    private final Map<Node, State> nodeStateMap;
    private final StateFactory states;
    private final SymbolFactory symbols;
    private final TokenFactory tokens;
    private final ActionFactory actions;

    private AutomatonCompiler(Machine machine) {
        this.machine = machine;
        this.nodeStateMap = new HashMap<>();
        this.states = new StateFactory();
        this.symbols = new SymbolFactory();
        this.tokens = new TokenFactory();
        this.actions = new ActionFactory(tokens);
    }

    private Automaton run() {
        var initial = compile(machine.source());

        return new Automaton(
                states.toArray(),
                symbols.toArray(),
                tokens.toArray(),
                actions.toArray(),
                initial
        );
    }

    private State compile(Node node) {
        var state = nodeStateMap.get(node);
        if (state != null) {
            return state;
        }

        state = states.createState(machine.targets().contains(node));

        nodeStateMap.put(node, state);

        var transitions = computeTransitions(node);

        state.setTransitions(transitions);

        return state;
    }

    private Transition[] computeTransitions(Node node) {
        var transitions = new ArrayList<Transition>();

        for (var link : machine.links().findAllFrom(node)) {
            var target = compile(link.getTarget());
            var pattern = link.getPattern();

            Symbol symbol;
            Token token;

            if (pattern instanceof PatternToken pt) {
                symbol = symbols.symbol(pt.pattern);
                token = tokens.token(pt.token);
            }
            else {
                symbol = symbols.symbol(pattern);
                token = tokens.empty();
            }

            transitions.add(new Transition(
                    target, symbol, token,
                    makeActions(link.getBeginOperations()),
                    makeActions(link.getEndOperations())));
        }

        transitions.sort(this::comparing);

        return transitions.toArray(transitions.toArray(new Transition[0]));
    }

    private int comparing(Transition left, Transition right) {
        // First: Not empty tokens
        var leftTokenEmpty = left.getToken().isEmpty();
        var rightTokenEmpty = right.getToken().isEmpty();

        if (leftTokenEmpty && !rightTokenEmpty) {
            return +1;
        }
        else if (!leftTokenEmpty && rightTokenEmpty) {
            return -1;
        }

        // Second: Not range symbols
        var leftSymbolRange = left.getSymbol() instanceof SymbolRange;
        var rightSymbolRange = right.getSymbol() instanceof SymbolRange;

        if (leftSymbolRange && rightSymbolRange) {
            var leftSymbolRangeV = (SymbolRange)left.getSymbol();
            var rightSymbolRangeV = (SymbolRange)right.getSymbol();
            return Character.compare(leftSymbolRangeV.getBegin(), rightSymbolRangeV.getBegin());
        }
        if (leftSymbolRange) {
            return +1;
        }
        else if (rightSymbolRange) {
            return -1;
        }

        // Third: order by char
        var leftSymbolChar = ((SymbolChar)left.getSymbol()).getValue();
        var rightSymbolChar = ((SymbolChar)right.getSymbol()).getValue();
        if (leftSymbolChar != rightSymbolChar) {
            return Character.compare(leftSymbolChar, rightSymbolChar);
        }

        // Finally, order by target
        var leftTargetIndex = states.indexOf(left.getTarget());
        var rightTargetIndex = states.indexOf(right.getTarget());
        return Integer.compare(leftTargetIndex, rightTargetIndex);
    }

    private Action[] makeActions(OperationList operations) {
        var result = new Action[operations.size()];

        for (var i = 0; i < operations.size(); i++) {
            result[i] = actions.create(operations.get(i));
        }

        return result;
    }

}
