package org.gramat;

import org.gramat.automata.Automaton;
import org.gramat.io.ExpressionFormatter;
import org.gramat.io.MachineFormatter;
import org.gramat.machine.nodes.NodeFactory;
import org.gramat.pipeline.AutomatonCompiler;
import org.gramat.pipeline.ExpressionCompiler;
import org.gramat.pipeline.ExpressionExpander;
import org.gramat.pipeline.ExpressionParser;
import org.gramat.pipeline.MachineCompiler;
import org.gramat.tools.CharInput;

public class Gramat {

    public Automaton compile(CharInput input) {
        return compile(input, "main");
    }

    public Automaton compile(CharInput input, String mainRule) {
        var map = ExpressionParser.parseFile(input);
        var expressionProgram = ExpressionExpander.run(map, mainRule);
        var nodeProvider = new NodeFactory();
        var machineProgram = ExpressionCompiler.run(nodeProvider, expressionProgram);
        var machine = MachineCompiler.run(nodeProvider, machineProgram);
        return AutomatonCompiler.run(machine);
    }

}
