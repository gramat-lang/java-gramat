package org.gramat;

import org.gramat.automating.engines.AutomatingEngine;
import org.gramat.automating.engines.EvalNodeEngine;
import org.gramat.automating.engines.LinkingEngine;
import org.gramat.automating.engines.MergingEngine;
import org.gramat.eval.EvalEngine;
import org.gramat.eval.EvalNode;
import org.gramat.eval.EvalProgram;
import org.gramat.exceptions.GramatException;
import org.gramat.expressions.ExpressionProgram;
import org.gramat.expressions.engines.ParsingEngine;
import org.gramat.expressions.engines.ResolvingEngine;
import org.gramat.inputs.Input;
import org.gramat.logging.Logger;
import org.gramat.logging.NullLogger;
import org.gramat.util.Debug;

public class Gramat {

    private final String DEFAULT_RULE_NAME = "main";
    private final Logger logger;

    public Gramat() {
        this(new NullLogger());
    }

    public Gramat(Logger logger) {
        this.logger = logger;
    }

    public EvalProgram compile(Input input) {
        return compile(input, DEFAULT_RULE_NAME);
    }

    public EvalProgram compile(Input input, String mainName) {
        var expressionProgram = ParsingEngine.run(input, mainName);

        return compile(expressionProgram);
    }

    public EvalProgram compile(ExpressionProgram program) {
        var resolvedProgram = ResolvingEngine.resolve(program, logger);

        Debug.print(resolvedProgram);

        var nMachine = AutomatingEngine.automate(resolvedProgram, logger);

        Debug.print(nMachine, false);

        var nLinked = LinkingEngine.run(nMachine, logger);

        Debug.print(nLinked, false);

        var dMachine = MergingEngine.resolve(nLinked, logger);

        Debug.print(dMachine, false);

        var eProgram = EvalNodeEngine.run(dMachine, logger);

        Debug.print(eProgram.node, true);

        return eProgram;
    }

    public Object eval(EvalNode node, Input input) {
        var engine = new EvalEngine(input, logger);

        var nodeHalt = engine.run(node);

        if (!nodeHalt.accepted) {
            throw new GramatException("rejected");
        }

        return engine.builder.pop();
    }
}
