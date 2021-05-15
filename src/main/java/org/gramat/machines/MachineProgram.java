package org.gramat.machines;

import org.gramat.tools.DataUtils;

import java.util.Map;

public class MachineProgram {

    public final Machine main;
    public final Map<String, Machine> dependencies;

    public MachineProgram(Machine main, Map<String, Machine> dependencies) {
        this.main = main;
        this.dependencies = DataUtils.immutableCopy(dependencies);
    }
}
