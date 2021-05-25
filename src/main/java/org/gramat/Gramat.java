package org.gramat;

import org.gramat.graphs.Automaton;
import org.gramat.graphs.CleanMachine;
import org.gramat.graphs.NodeProvider;
import org.gramat.pipeline.ExpressionCompiler;
import org.gramat.pipeline.ExpressionExpander;
import org.gramat.pipeline.ExpressionFormatter;
import org.gramat.pipeline.ExpressionParser;
import org.gramat.pipeline.MachineCompiler;
import org.gramat.pipeline.MachineFormatter;
import org.gramat.pipeline.MachineLinker;
import org.gramat.tools.CharInput;

public class Gramat {

    public CleanMachine compile(CharInput input) {
        return compile(input, "main");
    }

    public CleanMachine compile(CharInput input, String mainRule) {
        var map = ExpressionParser.parseFile(input);
        var expressionProgram = ExpressionExpander.run(map, mainRule);
        new ExpressionFormatter().writeProgram(System.out, expressionProgram, mainRule);
        var nodeProvider = new NodeProvider();
        var machineProgram = ExpressionCompiler.run(nodeProvider, expressionProgram);
        var machine = MachineLinker.run(nodeProvider, machineProgram);
        new MachineFormatter().writeMachine(System.out, machine);
        return machine;
    }

}
