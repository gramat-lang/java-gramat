package org.gramat.automating.engines;

import org.gramat.actions.design.ActionRole;
import org.gramat.actions.design.ActionScheme;
import org.gramat.actions.design.ActionTemplate;
import org.gramat.automating.Automaton;
import org.gramat.automating.Direction;
import org.gramat.automating.Machine;
import org.gramat.automating.State;
import org.gramat.exceptions.GramatException;
import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionProgram;
import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.groups.Cycle;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.literals.LiteralChar;
import org.gramat.expressions.literals.LiteralRange;
import org.gramat.expressions.literals.LiteralString;
import org.gramat.expressions.misc.ActionExpression;
import org.gramat.expressions.misc.Reference;
import org.gramat.expressions.misc.Wild;
import org.gramat.logging.Logger;

import java.util.Set;

public class AutomatingEngine {

    public static Machine automate(ExpressionProgram program, Logger logger) {
        var engine = new AutomatingEngine(logger);

        return engine.automate(program);
    }

    private final Logger logger;

    private int currentActionOrdinal;

    private AutomatingEngine(Logger logger) {
        this.logger = logger;
    }

    private Machine automate(ExpressionProgram program) {
        var am = new Automaton();

        // create empty machines for dependencies
        for (var name : program.dependencies.keySet()) {
            var machine = am.createMachine();

            am.machines.set(name, machine);
        }

        logger.debug("Creating main automaton...");

        var mainMachine = automateExpression(program.main, am);

        // connect empty machines
        for (var dependency : program.dependencies.entrySet()) {
            logger.debug("Creating automaton %s...", dependency.getKey());

            var outerMachine = am.machines.find(dependency.getKey());
            var innerMachine = automateExpression(dependency.getValue(), am);

            am.addEmpty(outerMachine.begin, innerMachine.begin);
            am.addEmpty(innerMachine.end, outerMachine.end);
        }

        return mainMachine;
    }

    private Machine automateExpression(Expression expr, Automaton am) {
        if (expr instanceof LiteralChar) {
            return automateLiteralChar((LiteralChar)expr, am);
        } else if (expr instanceof LiteralString) {
            return automateLiteralString((LiteralString)expr, am);
        } else if (expr instanceof LiteralRange) {
            return automateLiteralRange((LiteralRange)expr, am);
        } else if (expr instanceof Wild) {
            return automateWild((Wild)expr, am);
        } else if (expr instanceof Sequence) {
            return automateSequence((Sequence) expr, am);
        } else if (expr instanceof Alternation) {
            return automateAlternation((Alternation) expr, am);
        } else if (expr instanceof Optional) {
            return automateOptional((Optional) expr, am);
        } else if (expr instanceof Cycle) {
            return automateCycle((Cycle) expr, am);
        } else if (expr instanceof ActionExpression) {
            return automateAction((ActionExpression) expr, am);
        } else if (expr instanceof Reference) {
            return automateReference((Reference) expr, am);
        } else {
            throw new GramatException("unsupported value: " + expr);
        }
    }

    private Machine automateLiteralChar(LiteralChar chr, Automaton am) {
        var begin = am.createState(chr.beginLocation);
        var end = am.createState(chr.endLocation);

        am.addSymbol(begin, end, am.getChar(chr.value));

        return am.createMachine(begin, end);
    }

    private Machine automateLiteralString(LiteralString str, Automaton am) {
        var begin = am.createState(str.beginLocation);
        var end = begin;

        for (var chr : str.value.toCharArray()) {
            var state = end;

            end = am.createState(str.endLocation);  // TODO this is not the real location

            am.addSymbol(state, end, am.getChar(chr));
        }

        return am.createMachine(begin, end);
    }

    private Machine automateLiteralRange(LiteralRange expr, Automaton am) {
        var begin = am.createState(expr.beginLocation);
        var end = am.createState(expr.endLocation);

        am.addSymbol(begin, end, am.getRange(expr.begin, expr.end));

        return am.createMachine(begin, end);
    }

    private Machine automateWild(Wild wild, Automaton am) {
        var state = am.createWild(Set.of(wild.beginLocation, wild.endLocation));

        return am.createMachine(state, state);
    }

    private Machine automateSequence(Sequence seq, Automaton am) {
        State begin = null;
        State end = null;

        for (var item : seq.items) {
            var itemMachine = automateExpression(item, am);

            if (begin == null) {
                begin = itemMachine.begin;
                end = itemMachine.end;
            }
            else {
                am.addEmpty(end, itemMachine.begin);
                end = itemMachine.end;
            }
        }

        return am.createMachine(begin, end);
    }

    private Machine automateAlternation(Alternation alt, Automaton am) {
        if (alt.items.size() == 1) {
            return automateExpression(alt.items.get(0), am);
        }

        var begin = am.createState(alt.beginLocation);
        var end = am.createState(alt.endLocation);

        for (var item : alt.items) {
            var itemMachine = automateExpression(item, am);

            am.addEmpty(begin, itemMachine.begin);
            am.addEmpty(itemMachine.end, end);
        }

        return am.createMachine(begin, end);
    }

    private Machine automateOptional(Optional opt, Automaton am) {
        var begin = am.createState(opt.beginLocation);
        var end = am.createState(opt.endLocation);

        var contentMachine = automateExpression(opt.content, am);

        am.addEmpty(begin, end);
        am.addEmpty(begin, contentMachine.begin);
        am.addEmpty(contentMachine.end, end);

        return am.createMachine(begin, end);
    }

    private Machine automateCycle(Cycle expr, Automaton am) {
        var begin = am.createState(expr.beginLocation);
        var loopBegin = am.createState(expr.beginLocation);
        var loopEnd = am.createState(expr.endLocation);
        var end = am.createState(expr.endLocation);

        var loopMachine = automateExpression(expr.content, am);

        am.addEmpty(begin, loopBegin);

        am.addEmpty(loopBegin, loopMachine.begin);
        am.addEmpty(loopMachine.end, loopEnd);

        am.addEmpty(loopEnd, loopBegin);

        am.addEmpty(loopEnd, end);

        return am.createMachine(begin, end);
    }

    private Machine automateAction(ActionExpression action, Automaton am) {
        var beginState = am.createState(action.beginLocation);
        var beginAction = new ActionTemplate(
                Set.of(action.beginLocation),
                action.scheme,
                ActionRole.BEGIN,
                nextActionOrdinal(),
                action.argument);
        var contentMachine = automateExpression(action.content, am);
        var endAction = new ActionTemplate(
                Set.of(action.beginLocation),
                action.scheme,
                ActionRole.END,
                nextActionOrdinal(),
                action.argument);
        var endState = am.createState(action.endLocation);

        am.addAction(beginState, contentMachine.begin, beginAction, Direction.FORWARD);
        am.addAction(contentMachine.end, endState, endAction, Direction.BACKWARD);

        return am.createMachine(beginState, endState);
    }

    private Machine automateReference(Reference ref, Automaton am) {
        var begin = am.createState(ref.beginLocation);
        var end = am.createState(ref.endLocation);
        var heapToken = ref.name;
        var level = am.createLevel();
        var beginAction = new ActionTemplate(
                Set.of(ref.beginLocation),
                ActionScheme.WRAP_RECURSION,
                ActionRole.BEGIN,
                nextActionOrdinal(),
                heapToken);
        var endAction = new ActionTemplate(
                Set.of(ref.beginLocation),
                ActionScheme.WRAP_RECURSION,
                ActionRole.END,
                nextActionOrdinal(),
                heapToken);

        am.addReference(begin, end, ref.name, level, beginAction, endAction);

        return am.createMachine(begin, end);
    }

    private int nextActionOrdinal() {
        currentActionOrdinal++;
        return currentActionOrdinal;
    }

}
