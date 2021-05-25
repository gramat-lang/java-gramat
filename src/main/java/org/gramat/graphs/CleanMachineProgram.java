package org.gramat.graphs;

import java.util.Map;

public class CleanMachineProgram {
    public final CleanMachine main;
    public final Map<String, CleanMachine> dependencies;

    public CleanMachineProgram(CleanMachine main, Map<String, CleanMachine> dependencies) {
        this.main = main;
        this.dependencies = dependencies;
    }
}
