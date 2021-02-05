package org.gramat.eval;

import org.gramat.actions.Action;
import org.gramat.codes.Code;

import java.util.Objects;

public class EvalLink {
    public final Code code;
    public final EvalNode target;
    public final Action[] begin;
    public final Action[] end;

    public EvalLink(Code code, EvalNode target, Action[] begin, Action[] end) {
        this.code = Objects.requireNonNull(code);
        this.target = Objects.requireNonNull(target);
        this.begin = begin;
        this.end = end;
    }
}
