package org.gramat;

import org.gramat.machines.Automaton;
import org.gramat.machines.Machine;
import org.gramat.pipeline.ExpressionCompiler;
import org.gramat.pipeline.ExpressionExpander;
import org.gramat.pipeline.ExpressionParser;
import org.gramat.pipeline.MachineCompiler;
import org.gramat.pipeline.MachineFormatter;
import org.gramat.pipeline.MachineLinker;
import org.gramat.tools.CharInput;

public class Gramat {

    public Automaton compile(CharInput input) {
        return compile(input, "main");
    }

    public Automaton compile(CharInput input, String mainRule) {
        var map = ExpressionParser.parseFile(input);
        var expressionProgram = ExpressionExpander.run(map, mainRule);
        var machineProgram = ExpressionCompiler.run(expressionProgram);
//        new MachineFormatter().writeProgram(System.out, machineProgram);
        var machine = MachineLinker.run(machineProgram);
        new MachineFormatter().writeMachine(System.out, machine);
        return MachineCompiler.compile(machine);
    }

}
