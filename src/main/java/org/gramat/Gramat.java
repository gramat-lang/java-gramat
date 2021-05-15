package org.gramat;

import org.gramat.machines.Machine;
import org.gramat.pipeline.ExpressionCompiler;
import org.gramat.pipeline.ExpressionExpander;
import org.gramat.pipeline.ExpressionParser;
import org.gramat.pipeline.MachineFormatter;
import org.gramat.pipeline.MachineLinker;
import org.gramat.tools.CharInput;

public class Gramat {

    public Machine compile(CharInput input) {
        return compile(input, "main");
    }

    public Machine compile(CharInput input, String mainRule) {
        var map = ExpressionParser.parseFile(input);
        var program = ExpressionExpander.run(map, mainRule);
        var machineProgram = ExpressionCompiler.run(program);
        return MachineLinker.run(machineProgram);
    }

}
