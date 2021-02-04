package org.gramat;

import org.gramat.automating.engines.AutomatingEngine;
import org.gramat.automating.engines.DeterministicEngine;
import org.gramat.automating.engines.EmptyResolverEngine;
import org.gramat.automating.engines.LinkingEngine;
import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionProgram;
import org.gramat.inputs.Input;
import org.gramat.logging.Logger;
import org.gramat.logging.NullLogger;
import org.gramat.parsing.ParsingEngine;
import org.gramat.resolving.ResolvingEngine;
import org.gramat.util.Debug;
import org.gramat.util.ExpressionMap;

public class Gramat {

    private final String DEFAULT_RULE_NAME = "main";
    private final Logger logger;

    public Gramat() {
        this(new NullLogger());
    }

    public Gramat(Logger logger) {
        this.logger = logger;
    }

    public Object compile(Input input) {
        return compile(input, DEFAULT_RULE_NAME);
    }

    public Object compile(Input input, String ruleName) {
        var parser = new ParsingEngine();

        parser.readAll(input);

        var rules = parser.getRules();
        var main = rules.get(ruleName);

        return compile(main, rules);
    }

    public Object compile(Expression main, ExpressionMap dependencies) {
        return compile(new ExpressionProgram(main, dependencies));
    }

    public Object compile(ExpressionProgram program) {
        var resolver = new ResolvingEngine(logger);

        var resolvedProgram = resolver.resolve(program);

        Debug.print(resolvedProgram);

        var automator = new AutomatingEngine(logger);

        var nMachine = automator.automate(resolvedProgram);

        Debug.print(nMachine, false);

        var markEngine = new LinkingEngine(logger);

        var nLinked = markEngine.resolve(nMachine);

        Debug.print(nLinked, true);

//        var nClean = new EmptyResolverEngine(logger).resolve(nLinked);
//        Debug.print(nClean, true);

        var dEngine = new DeterministicEngine(logger);
        var dMachine = dEngine.resolve(nLinked);

        Debug.print(dMachine, false);

        return nMachine;
    }

}
