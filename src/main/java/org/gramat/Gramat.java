package org.gramat;

import org.gramat.graphs.Automaton;
import org.gramat.pipeline.ExpressionCompiler;
import org.gramat.pipeline.ExpressionExpander;
import org.gramat.pipeline.ExpressionFormatter;
import org.gramat.pipeline.ExpressionParser;
import org.gramat.pipeline.MachineCompiler;
import org.gramat.pipeline.MachineFormatter;
import org.gramat.tools.CharInput;

public class Gramat {

    public Automaton compile(CharInput input) {
        return compile(input, "main");
    }

    public Automaton compile(CharInput input, String mainRule) {
        var map = ExpressionParser.parseFile(input);
        var expressionProgram = ExpressionExpander.run(map, mainRule);
        new ExpressionFormatter().writeProgram(System.out, expressionProgram, mainRule);
        var machine = ExpressionCompiler.run(expressionProgram);
        new MachineFormatter().writeMachine(System.out, machine);
        return MachineCompiler.compile(machine);
    }

}
