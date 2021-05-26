package org.gramat;

import org.gramat.machine.Machine;
import org.gramat.machine.nodes.NodeFactory;
import org.gramat.pipeline.ExpressionCompiler;
import org.gramat.pipeline.ExpressionExpander;
import org.gramat.pipeline.ExpressionFormatter;
import org.gramat.pipeline.ExpressionParser;
import org.gramat.pipeline.MachineCompiler;
import org.gramat.pipeline.MachineFormatter;
import org.gramat.tools.CharInput;

public class Gramat {

    public Machine compile(CharInput input) {
        return compile(input, "main");
    }

    public Machine compile(CharInput input, String mainRule) {
        var map = ExpressionParser.parseFile(input);
        var expressionProgram = ExpressionExpander.run(map, mainRule);
        new ExpressionFormatter().writeProgram(System.out, expressionProgram, mainRule);
        var nodeProvider = new NodeFactory();
        var machineProgram = ExpressionCompiler.run(nodeProvider, expressionProgram);
        new MachineFormatter().write(System.out, machineProgram);
        var machine = MachineCompiler.run(nodeProvider, machineProgram);
        new MachineFormatter().writeMachine(System.out, machine);
        return machine;
    }

}
