package org.gramat.io;

import org.gramat.automata.Automaton;
import org.gramat.automata.State;
import org.gramat.automata.Transition;

public class AutomatonFormatter {

    public static String toString(Automaton automaton) {
        var buffer = new StringBuilder();
        new AutomatonFormatter(buffer).write(automaton);
        return buffer.toString();
    }

    private final AmWriter output;

    public AutomatonFormatter(Appendable output) {
        this.output = new AmWriter(output);
    }

    public void write(Automaton automaton) {
        // Initial

        output.writeInitial(name(automaton.getInitial(), automaton));

        // Transitions

        for (var state : automaton.getStates()) {
            var source = name(state, automaton);

            for (var transition : state.getTransitions()) {
                var target = name(transition.getTarget(), automaton);
                var label = computeLabel(transition);

                output.writeTransition(source, target, label);
            }
        }

        // Accepteds

        for (var state : automaton.getStates()) {
            if (state.isAccepted()) {
                output.writeAccepted(name(state, automaton));
            }
        }
    }

    private static String computeLabel(Transition transition) {
        var label = new StringBuilder();

        for (var action : transition.getBeginActions()) {
            label.append(action);
            label.append('\n');
        }

        label.append(transition.getSymbol());

        if (!transition.getToken().isEmpty()) {
            label.append(" / ");
            label.append(transition.getToken());
        }

        for (var action : transition.getEndActions()) {
            label.append('\n');
            label.append(action);
        }

        return label.toString();
    }

    private static String name(State state, Automaton automaton) {
        return String.valueOf(automaton.findIndex(state)+1);
    }

}
