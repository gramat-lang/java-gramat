package org.gramat.machines;

import org.gramat.tools.DataUtils;

import java.util.List;
import java.util.Objects;

public class Machine {

    public final Node source;
    public final Node target;
    public final List<Link> links;

    public Machine(Node source, Node target, List<Link> links) {
        this.source = Objects.requireNonNull(source);
        this.target = Objects.requireNonNull(target);
        this.links = DataUtils.immutableCopy(links);
    }
}
