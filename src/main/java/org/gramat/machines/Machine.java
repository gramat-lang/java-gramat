package org.gramat.machines;

import org.gramat.tools.Validations;
import org.gramat.tools.DataUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Machine {

    public final Node source;
    public final Set<Node> targets;
    public final List<Link> links;

    public Machine(Node source, Set<Node> targets, List<Link> links) {
        Validations.notEmpty(targets);

        this.source = Objects.requireNonNull(source);
        this.targets = DataUtils.immutableCopy(targets);
        this.links = DataUtils.immutableCopy(links);
    }
}
