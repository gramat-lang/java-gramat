package org.gramat.eval;

import org.gramat.actions.Action;
import org.gramat.inputs.Input;
import org.gramat.logging.Logger;
import org.gramat.makers.Maker;
import org.gramat.util.NamedMap;
import org.gramat.util.PP;

public class EvalEngine {

    public final Input input;
    public final Logger logger;
    public final EvalHeap heap;
    public final NamedMap<Maker> makers; // TODO create specialized map
    public final EvalBuilder builder;

    public EvalEngine(Input input, Logger logger) {
        this.input = input;
        this.logger = logger;
        this.heap = new EvalHeap();
        this.makers = new NamedMap<>();
        this.builder = new EvalBuilder(this);
    }

    public EvalNode run(EvalNode begin) {
        var node = begin;

        while (true) {
            var c = input.peek();

            var link = findLink(node, c);
            if (link == null) {
                logger.debug("Halt.");
                break;
            }

            logger.debug("Moving from %s to %s with %s...", node.id, link.target.id, PP.str(c));

            execute(link.begin);

            input.pull();

            execute(link.end);

            node = link.target;
        }

        return node;
    }

    private EvalLink findLink(EvalNode node, char c) {
        if (node.links != null) {
            for (var link : node.links) {
                if (link.code.test(c)) {
                    return link;
                }
            }
        }
        return null;
    }

    private void execute(Action[] actions) {
        if (actions != null) {
            for (var action : actions) {
                logger.debug("Executing %s...", action);

                action.execute(this);
            }
        }
    }

}
