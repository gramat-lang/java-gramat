package org.gramat.condensing;

import org.gramat.condensing.rules.CondenseAlternation;
import org.gramat.condensing.rules.CondenseNameWrapper;
import org.gramat.condensing.rules.CondenseOptional;
import org.gramat.condensing.rules.CondenseRepetition;
import org.gramat.condensing.rules.CondenseSequence;
import org.gramat.condensing.rules.CondenseLiteralString;
import org.gramat.condensing.rules.CondenseListWrapper;
import org.gramat.condensing.rules.CondenseObjectWrapper;
import org.gramat.condensing.rules.CondensePropertyWrapper;
import org.gramat.condensing.rules.CondenseTextWrapper;
import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionProgram;
import org.gramat.expressions.actions.ListBegin;
import org.gramat.expressions.actions.ListEnd;
import org.gramat.expressions.actions.NameBegin;
import org.gramat.expressions.actions.NameEnd;
import org.gramat.expressions.actions.ObjectBegin;
import org.gramat.expressions.actions.ObjectEnd;
import org.gramat.expressions.actions.PropertyBegin;
import org.gramat.expressions.actions.PropertyEnd;
import org.gramat.expressions.actions.TextBegin;
import org.gramat.expressions.actions.TextEnd;
import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Repetition;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.literals.LiteralChar;
import org.gramat.expressions.literals.LiteralRange;
import org.gramat.expressions.misc.Reference;
import org.gramat.expressions.misc.Wild;
import org.gramat.logging.Logger;
import org.gramat.util.ExpressionMap;

public class CondensingEngine {

    private final Logger logger;
    private final CondensingPipeline pipeline;

    public CondensingEngine(Logger logger) {
        this.logger = logger;
        this.pipeline = new CondensingPipeline(logger);

        // Condense literals
        pipeline.addRule(new CondenseLiteralString());

        // Condense wrappers
        pipeline.addRule(new CondenseTextWrapper());
        pipeline.addRule(new CondenseObjectWrapper());
        pipeline.addRule(new CondenseListWrapper());
        pipeline.addRule(new CondensePropertyWrapper());
        pipeline.addRule(new CondenseNameWrapper());

        // Condense groups
        pipeline.addRule(new CondenseAlternation());
        pipeline.addRule(new CondenseSequence());
        pipeline.addRule(new CondenseOptional());
        pipeline.addRule(new CondenseRepetition());

        // Allowed condensed expressions

        // Groups
        pipeline.addResultType(Sequence.class);
        pipeline.addResultType(Alternation.class);
        pipeline.addResultType(Repetition.class);
        pipeline.addResultType(Optional.class);

        // Literals
        pipeline.addResultType(LiteralChar.class);
        pipeline.addResultType(LiteralRange.class);

        // Actions
        pipeline.addResultType(TextBegin.class);
        pipeline.addResultType(TextEnd.class);
        pipeline.addResultType(ObjectBegin.class);
        pipeline.addResultType(ObjectEnd.class);
        pipeline.addResultType(ListBegin.class);
        pipeline.addResultType(ListEnd.class);
        pipeline.addResultType(PropertyBegin.class);
        pipeline.addResultType(PropertyEnd.class);
        pipeline.addResultType(NameBegin.class);
        pipeline.addResultType(NameEnd.class);

        // Misc
        pipeline.addResultType(Reference.class);
        pipeline.addResultType(Wild.class);
    }

    public ExpressionProgram condense(ExpressionProgram program) {
        logger.debug("Condensing main expression %s...", program.main);

        var condensedMain = condenseExpression(program.main);

        var condensedDependencies = new ExpressionMap();

        for (var dependency : program.dependencies.entrySet()) {
            logger.debug("Condensing dependency %s...", dependency.getKey());

            var condensedDependency = condenseExpression(dependency.getValue());

            condensedDependencies.set(dependency.getKey(), condensedDependency);
        }

        return new ExpressionProgram(condensedMain, condensedDependencies);
    }

    private Expression condenseExpression(Expression expression) {
        var cc = new CondensingContext(pipeline);

        var result = pipeline.apply(expression, cc);

        logger.debug("Condensing done in %s seconds.", cc.getElapsedSeconds());

        return result;
    }

}
