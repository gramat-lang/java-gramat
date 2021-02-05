package org.gramat.formatting;

import org.gramat.actions.Action;
import org.gramat.eval.EvalNode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;

public class EvalFormatter extends AmFormatter {
    public EvalFormatter(Appendable out) {
        super(out);
    }

    public void write(EvalNode initial) {
        var queue = new ArrayDeque<EvalNode>();
        var control = new HashSet<EvalNode>();

        queue.add(initial);

        writeInitial(nameOf(initial));

        do {
            var node = queue.remove();
            if (control.add(node)) {
                if (node.links != null) {
                    for (var link : node.links) {
                        var symbols = new ArrayList<String>();

                        fillActions(link.begin, symbols);
                        symbols.add(link.code.toString());
                        fillActions(link.end, symbols);

                        writeTransition(nameOf(node), nameOf(link.target), symbols);

                        queue.add(link.target);
                    }
                }

                if (node.accepted) {
                    writeAccepted(nameOf(node));
                }
            }
        } while (!queue.isEmpty());
    }

    private void fillActions(Action[] actions, ArrayList<String> symbols) {
        if (actions == null) {
            return;
        }

        for (var action : actions) {
            symbols.add(action.toString());
        }
    }

    private String nameOf(EvalNode node) {
        return "N" + node.id;
    }
}
