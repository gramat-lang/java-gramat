package org.gramat.automating.engines;

import org.gramat.actions.Action;
import org.gramat.actions.ActionList;
import org.gramat.actions.ListEnd;
import org.gramat.actions.NameBegin;
import org.gramat.actions.NameEnd;
import org.gramat.actions.ObjectBegin;
import org.gramat.actions.ObjectEnd;
import org.gramat.actions.PropertyBegin;
import org.gramat.actions.PropertyEnd;
import org.gramat.actions.TextBegin;
import org.gramat.actions.TextEnd;
import org.gramat.automating.Automaton;
import org.gramat.automating.Direction;
import org.gramat.automating.Level;
import org.gramat.automating.Machine;
import org.gramat.automating.State;
import org.gramat.exceptions.GramatException;
import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionProgram;
import org.gramat.actions.ListBegin;
import org.gramat.expressions.actions.ListWrapper;
import org.gramat.expressions.actions.NameWrapper;
import org.gramat.expressions.actions.ObjectWrapper;
import org.gramat.expressions.actions.PropertyWrapper;
import org.gramat.expressions.actions.TextWrapper;
import org.gramat.expressions.groups.Alternation;
import org.gramat.expressions.groups.Optional;
import org.gramat.expressions.groups.Repetition;
import org.gramat.expressions.groups.Sequence;
import org.gramat.expressions.literals.LiteralChar;
import org.gramat.expressions.literals.LiteralRange;
import org.gramat.expressions.literals.LiteralString;
import org.gramat.expressions.misc.Reference;
import org.gramat.expressions.misc.Wild;
import org.gramat.logging.Logger;

public class AutomatingEngine {

    public static Machine automate(ExpressionProgram program, Logger logger) {
        var engine = new AutomatingEngine(logger);

        return engine.automate(program);
    }

    private final Logger logger;

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
            return automateWild(am);
        } else if (expr instanceof Sequence) {
            return automateSequence((Sequence) expr, am);
        } else if (expr instanceof Alternation) {
            return automateAlternation((Alternation) expr, am);
        } else if (expr instanceof Optional) {
            return automateOptional((Optional) expr, am);
        } else if (expr instanceof Repetition) {
            return automateRepetition((Repetition) expr, am);
        } else if (expr instanceof ListWrapper) {
            return automateListWrapper((ListWrapper) expr, am);
        } else if (expr instanceof ObjectWrapper) {
            return automateObjectWrapper((ObjectWrapper) expr, am);
        } else if (expr instanceof PropertyWrapper) {
            return automatePropertyWrapper((PropertyWrapper) expr, am);
        } else if (expr instanceof TextWrapper) {
            return automateTextWrapper((TextWrapper) expr, am);
        } else if (expr instanceof NameWrapper) {
            return automateNameWrapper((NameWrapper) expr, am);
        } else if (expr instanceof Reference) {
            return automateReference((Reference) expr, am);
        } else {
            throw new GramatException("unsupported value: " + expr);
        }
    }

    private Machine automateLiteralChar(LiteralChar chr, Automaton am) {
        var begin = am.createState();
        var end = am.createState();

        am.addSymbol(begin, end, am.getChar(chr.value));

        return am.createMachine(begin, end);
    }

    private Machine automateLiteralString(LiteralString str, Automaton am) {
        var begin = am.createState();
        var end = begin;

        for (var chr : str.value.toCharArray()) {
            var state = end;

            end = am.createState();

            am.addSymbol(state, end, am.getChar(chr));
        }

        return am.createMachine(begin, end);
    }

    private Machine automateLiteralRange(LiteralRange expr, Automaton am) {
        var begin = am.createState();
        var end = am.createState();

        am.addSymbol(begin, end, am.getRange(expr.begin, expr.end));

        return am.createMachine(begin, end);
    }

    private Machine automateWild(Automaton am) {
        var state = am.createWild();

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

        var begin = am.createState();
        var end = am.createState();

        for (var item : alt.items) {
            var itemMachine = automateExpression(item, am);

            am.addEmpty(begin, itemMachine.begin);
            am.addEmpty(itemMachine.end, end);
        }

        return am.createMachine(begin, end);
    }

    private Machine automateOptional(Optional opt, Automaton am) {
        var begin = am.createState();
        var end = am.createState();

        var contentMachine = automateExpression(opt.content, am);

        am.addEmpty(begin, end);
        am.addEmpty(begin, contentMachine.begin);
        am.addEmpty(contentMachine.end, end);

        return am.createMachine(begin, end);
    }

    private Machine automateRepetition(Repetition rep, Automaton am) {
        var begin = am.createState();
        var loopBegin = am.createState();
        var loopEnd = am.createState();
        var end = am.createState();

        var loopMachine = automateExpression(rep.content, am);

        am.addEmpty(begin, end);
        am.addEmpty(begin, loopBegin);

        am.addEmpty(loopBegin, loopMachine.begin);
        am.addEmpty(loopMachine.end, loopEnd);

        am.addEmpty(loopEnd, loopBegin);

        am.addEmpty(loopEnd, end);

        return am.createMachine(begin, end);
    }

    private Machine automateListWrapper(ListWrapper wrapper, Automaton am) {
        return automateWrapper(new ListBegin(), wrapper.content, new ListEnd(wrapper.typeHint), am);
    }

    private Machine automateObjectWrapper(ObjectWrapper wrapper, Automaton am) {
        return automateWrapper(new ObjectBegin(), wrapper.content, new ObjectEnd(wrapper.typeHint), am);
    }

    private Machine automatePropertyWrapper(PropertyWrapper wrapper, Automaton am) {
        return automateWrapper(new PropertyBegin(), wrapper.content, new PropertyEnd(wrapper.nameHint), am);
    }

    private Machine automateTextWrapper(TextWrapper wrapper, Automaton am) {
        return automateWrapper(new TextBegin(), wrapper.content, new TextEnd(wrapper.parser), am);
    }

    private Machine automateNameWrapper(NameWrapper wrapper, Automaton am) {
        return automateWrapper(new NameBegin(), wrapper.content, new NameEnd(), am);
    }

    private Machine automateWrapper(Action beginAction, Expression content, Action endAction, Automaton am) {
        var machine = automateExpression(content, am);
        var begin = am.createState();
        var end = am.createState();

        am.addAction(begin, machine.begin, beginAction, Direction.FORWARD);
        am.addAction(machine.end, end, endAction, Direction.BACKWARD);

        return am.createMachine(begin, end);
    }

    private Machine automateReference(Reference ref, Automaton am) {
        var begin = am.createState();
        var end = am.createState();

        am.addReference(begin, end, ref.name, am.createLevel(), new ActionList(), new ActionList());

        return am.createMachine(begin, end);
    }

}
