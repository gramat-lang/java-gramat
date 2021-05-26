package org.gramat.machine;

import java.util.Map;

public class MachineProgram {
    public final Machine main;
    public final Map<String, Machine> dependencies;

    public MachineProgram(Machine main, Map<String, Machine> dependencies) {
        this.main = main;
        this.dependencies = dependencies;
    }
}
