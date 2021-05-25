package org.gramat.graphs;

import org.gramat.data.nodes.Nodes;
import org.gramat.graphs.links.Link;

/**
 * ->S
 * S -> S : S_S
 * S -> N : S_N
 * S -> T : S_T
 * N -> S : N_S
 * N -> N : N_N
 * N -> T : N_T
 * T -> S : T_S
 * T -> N : T_N
 * T -> T : T_T
 */
public enum Direction {
    S_S, S_T, S_N,
    T_S, T_T, T_N,
    N_S, N_T, N_N;

    public static Direction compute(Link link, Node sources, Nodes targets) {
        var fromSource = (sources == link.getSource());
        var fromTarget = targets.contains(link.getSource());
        var toSource = (sources == link.getTarget());
        var toTarget = targets.contains(link.getTarget());

        if (fromSource) {
            if (toSource) {
                return Direction.S_S;
            } else if (toTarget) {
                return Direction.S_T;
            } else {
                return Direction.S_N;
            }
        }
        else if (fromTarget) {
            if (toSource) {
                return Direction.T_S;
            } else if (toTarget) {
                return Direction.T_T;
            } else {
                return Direction.T_N;
            }
        }
        else {
            if (toSource) {
                return Direction.N_S;
            } else if (toTarget) {
                return Direction.N_T;
            } else {
                return Direction.N_N;
            }
        }
    }
}
