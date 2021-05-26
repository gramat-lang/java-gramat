package org.gramat.pipeline;

import org.gramat.actions.ActionFactory;
import org.gramat.errors.ErrorFactory;
import org.gramat.expressions.Alternation;
import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionProgram;
import org.gramat.expressions.Literal;
import org.gramat.expressions.Option;
import org.gramat.expressions.Reference;
import org.gramat.expressions.Repeat;
import org.gramat.expressions.Sequence;
import org.gramat.expressions.Wildcard;
import org.gramat.expressions.Wrapping;
import org.gramat.machine.Machine;
import org.gramat.machine.MachineProgram;
import org.gramat.machine.links.LinkList;
import org.gramat.machine.nodes.Node;
import org.gramat.machine.nodes.NodeFactory;
import org.gramat.machine.nodes.NodeSet;
import org.gramat.patterns.PatternFactory;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class ExpressionCompiler {

    public record Segment(NodeSet sources, NodeSet targets) {}

    public static MachineProgram run(NodeFactory nodeFactory, ExpressionProgram program) {
        var dependencies = new LinkedHashMap<String, Machine>();
        var main = compileExpressionClean(nodeFactory, program.main);

        for (var entry : program.dependencies.entrySet()) {
            var dependency = compileExpressionClean(nodeFactory, entry.getValue());

            dependencies.put(entry.getKey(), dependency);
        }

        return new MachineProgram(main, dependencies);
    }

    private static Machine compileExpressionClean(NodeFactory nodeFactory, Expression expression) {
        var linkList = new LinkList();
        var dirtySegment = compileExpression(nodeFactory, linkList, expression);

        return MachineCleaner.run(
                nodeFactory,
                dirtySegment.sources(), dirtySegment.targets(),
                linkList);
    }

    private static Segment compileExpression(NodeFactory nodeFactory, LinkList linkList, Expression expression) {
        if (expression instanceof Alternation) {
            return compileAlternation(nodeFactory, linkList, (Alternation)expression);
        }
        else if (expression instanceof Option) {
            return compileOption(nodeFactory, linkList, (Option)expression);
        }
        else if (expression instanceof Reference) {
            return compileReference(nodeFactory, linkList, (Reference)expression);
        }
        else if (expression instanceof Repeat) {
            return compileRepeat(nodeFactory, linkList, (Repeat)expression);
        }
        else if (expression instanceof Sequence) {
            return compileSequence(nodeFactory, linkList, (Sequence)expression);
        }
        else if (expression instanceof Literal) {
            return compileLiteral(nodeFactory, linkList, (Literal)expression);
        }
        else if (expression instanceof Wildcard) {
            return compileWildcard(nodeFactory, linkList, (Wildcard)expression);
        }
        else if (expression instanceof Wrapping) {
            return compileWrapping(nodeFactory, linkList, (Wrapping)expression);
        }
        else {
            throw ErrorFactory.internalError("not implemented expression: " + expression);
        }
    }

    private static Segment compileAlternation(NodeFactory nodeFactory, LinkList linkList, Alternation alternation) {
        var sources = new LinkedHashSet<Node>();
        var targets = new LinkedHashSet<Node>();

        for (var item : alternation.items) {
            var itemMachine = compileExpression(nodeFactory, linkList, item);

            sources.addAll(itemMachine.sources().toCollection());
            targets.addAll(itemMachine.targets().toCollection());
        }

        return new Segment(NodeSet.of(sources), NodeSet.of(targets));
    }

    private static Segment compileOption(NodeFactory nodeFactory, LinkList linkList, Option option) {
        var machine = compileExpression(nodeFactory, linkList, option.content);
        return new Segment(
                machine.sources(),
                machine.sources().join(machine.targets()));
    }

    private static Segment compileReference(NodeFactory nodeFactory, LinkList linkList, Reference reference) {
        var source = nodeFactory.createNode();
        var target = nodeFactory.createNode();
        var pattern = PatternFactory.reference(reference.name);

        linkList.createLink(source, target, pattern);

        return new Segment(NodeSet.of(source), NodeSet.of(target));
    }

    private static Segment compileRepeat(NodeFactory nodeFactory, LinkList linkList, Repeat repeat) {
        var source = nodeFactory.createNode();
        var target = nodeFactory.createNode();

        var contentMachine = compileExpression(nodeFactory, linkList, repeat.content);

        linkList.createLink(source, contentMachine.sources());
        linkList.createLink(contentMachine.targets(), target);

        if (repeat.separator != null) {
            var separatorMachine = compileExpression(nodeFactory, linkList, repeat.separator);

            linkList.createLink(contentMachine.targets(), separatorMachine.sources());
            linkList.createLink(separatorMachine.targets(), contentMachine.sources());
        }
        else {
            linkList.createLink(contentMachine.targets(), contentMachine.sources());
        }

        return new Segment(NodeSet.of(source), NodeSet.of(target));
    }

    private static Segment compileSequence(NodeFactory nodeFactory, LinkList linkList, Sequence sequence) {
        var source = nodeFactory.createNode();
        var lastNode = source;

        for (var item : sequence.items) {
            var itemMachine = compileExpression(nodeFactory, linkList, item);

            linkList.createLink(lastNode, itemMachine.sources());

            lastNode = nodeFactory.createNode();

            linkList.createLink(itemMachine.targets(), lastNode);
        }

        return new Segment(NodeSet.of(source), NodeSet.of(lastNode));
    }

    private static Segment compileLiteral(NodeFactory nodeFactory, LinkList linkList, Literal literal) {
        var source = nodeFactory.createNode();
        var target = nodeFactory.createNode();

        linkList.createLink(source, target, literal.pattern);

        return new Segment(NodeSet.of(source), NodeSet.of(target));
    }

    private static Segment compileWildcard(NodeFactory nodeFactory, LinkList linkList, Wildcard wildcard) {
        throw new UnsupportedOperationException();
    }

    private static Segment compileWrapping(NodeFactory nodeFactory, LinkList linkList, Wrapping wrapping) {
        var machine = compileExpressionClean(nodeFactory, wrapping.content);
        var begin = ActionFactory.createBeginAction(wrapping.type);
        var end = ActionFactory.createEndAction(wrapping.type, wrapping.argument);

        for (var link : machine.links()) {
            if (machine.source() == link.getSource()) {
                link.addBeforeActions(begin);
            }

            if (machine.targets().contains(link.getTarget())) {
                link.addAfterActions(end);
            }
        }

        linkList.addLinks(machine.links());

        return new Segment(NodeSet.of(machine.source()), machine.targets());
    }

    private ExpressionCompiler() {}

}
