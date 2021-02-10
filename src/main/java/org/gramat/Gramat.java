package org.gramat;

import org.gramat.automating.engines.AutomatingEngine;
import org.gramat.automating.engines.DetEngine;
import org.gramat.automating.engines.EvalNodeEngine;
import org.gramat.automating.engines.LinkingEngine;
import org.gramat.automating.engines.MergingEngine;
import org.gramat.automating.engines.ValidationEngine;
import org.gramat.eval.EvalEngine;
import org.gramat.eval.EvalNode;
import org.gramat.eval.EvalProgram;
import org.gramat.exceptions.EvalException;
import org.gramat.expressions.ExpressionProgram;
import org.gramat.expressions.engines.ParsingEngine;
import org.gramat.expressions.transform.TransformEngine;
import org.gramat.inputs.Input;
import org.gramat.logging.Logger;
import org.gramat.logging.NullLogger;
import org.gramat.util.Debug;

import java.io.InputStream;

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
        var resolvedProgram = TransformEngine.run(program, logger);

        Debug.print(resolvedProgram);

        var eMachine = AutomatingEngine.automate(resolvedProgram, logger);

        Debug.print(eMachine, false);

        var eLinked = LinkingEngine.run(eMachine, logger);

        Debug.print(eLinked, false);

        var nMachine = MergingEngine.resolve(eLinked, logger);

        Debug.print(nMachine, false);

        var dMachine = DetEngine.run(nMachine, logger);

        Debug.print(dMachine, false);

        var eProgram = EvalNodeEngine.run(dMachine, logger);

        Debug.print(eProgram.node, true);

        ValidationEngine.validate(eProgram.node);

        return eProgram;
    }

    public Object eval(EvalNode node, Input input) {
        var engine = new EvalEngine(input, logger);

        var nodeHalt = engine.run(node);

        if (!nodeHalt.accepted) {
            throw new EvalException("rejected " + input.getLocation(), null, nodeHalt.id);
        }

        return engine.builder.pop();
    }

    public EvalProgram loadProgram(InputStream stream) {
        throw new UnsupportedOperationException();
    }

    public void saveProgram(EvalProgram program) {
        // TODO
    }
}
