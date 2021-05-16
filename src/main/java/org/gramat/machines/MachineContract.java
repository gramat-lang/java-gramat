package org.gramat.machines;

import org.gramat.tools.DataUtils;
import org.gramat.tools.Validations;

import java.util.List;
import java.util.Set;

public class MachineContract {

    public final Set<Node> sources;
    public final Set<Node> targets;
    public final List<Link> links;

    public MachineContract(Set<Node> sources, Set<Node> targets, List<Link> links) {
        Validations.notEmpty(sources);
        Validations.notEmpty(targets);

        this.sources = DataUtils.immutableCopy(sources);
        this.targets = DataUtils.immutableCopy(targets);
        this.links = DataUtils.immutableCopy(links);
    }
}
