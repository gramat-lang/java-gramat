package org.gramat.automating.engines;

import org.gramat.eval.EvalNode;
import org.gramat.exceptions.GramatException;

import java.util.ArrayDeque;
import java.util.HashSet;

public interface ValidationEngine {

    static void validate(EvalNode root) {
        var queue = new ArrayDeque<EvalNode>();
        var control = new HashSet<EvalNode>();
        var accepted = false;

        queue.add(root);

        do {
            var node = queue.remove();

            if (control.add(node)) {
                if (node.accepted) {
                    accepted = true;
                }

                if (node.links != null) {
                    for (var i = 0; i < node.links.length; i++) {
                        var iLink = node.links[i];

                        queue.add(iLink.target);

                        for (var j = 0; j < node.links.length; j++) {
                            if (i != j) {
                                var jLink = node.links[j];

                                if (iLink.code.intersects(jLink.code)) {
                                    throw new GramatException("Node " + node.id + ": " + iLink.code + " intersects " + jLink.code);
                                }
                                else if (jLink.code.intersects(iLink.code)) {
                                    throw new GramatException("Node " + node.id + ": " + jLink.code + " intersects " + iLink.code);
                                }
                            }
                        }
                    }
                }
            }
        } while (!queue.isEmpty());

        if (!accepted) {
            throw new GramatException("missing accepted node");
        }
    }

}
