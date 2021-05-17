package org.gramat.graphs;

import org.gramat.data.Nodes;
import org.gramat.tools.DataUtils;
import org.gramat.tools.Validations;

import java.util.List;

public class MachineContract {

    public final Nodes sources;
    public final Nodes targets;
    public final List<Link> links;

    public MachineContract(Nodes sources, Nodes targets, List<Link> links) {
        Validations.notEmpty(sources);
        Validations.notEmpty(targets);

        this.sources = sources.copyR();
        this.targets = targets.copyR();
        this.links = DataUtils.immutableCopy(links);
    }
}
