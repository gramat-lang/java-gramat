package org.gramat.pipeline;

import org.gramat.machine.operations.OperationFactory;
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
import org.gramat.machine.patterns.PatternFactory;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class ExpressionCompiler {

    public record Segment(NodeSet sources, NodeSet targets) {}

    public static MachineProgram run(NodeFactory nodeFactory, ExpressionProgram program) {
        return new ExpressionCompiler(nodeFactory).run(program);
    }

    private final NodeFactory nodeFactory;
    private final OperationFactory operationFactory;

    public ExpressionCompiler(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
        this.operationFactory = new OperationFactory();
    }

    private MachineProgram run(ExpressionProgram program) {
        var dependencies = new LinkedHashMap<String, Machine>();
        var main = compileExpressionClean(program.main);

        for (var entry : program.dependencies.entrySet()) {
            var dependency = compileExpressionClean(entry.getValue());

            dependencies.put(entry.getKey(), dependency);
        }

        return new MachineProgram(main, dependencies);
    }

    private Machine compileExpressionClean(Expression expression) {
        var linkList = new LinkList();
        var dirtySegment = compileExpression(linkList, expression);

        return PowersetConstruction.run(
                nodeFactory,
                dirtySegment.sources(), dirtySegment.targets(),
                linkList);
    }

    private Segment compileExpression(LinkList linkList, Expression expression) {
        if (expression instanceof Alternation) {
            return compileAlternation(linkList, (Alternation)expression);
        }
        else if (expression instanceof Option) {
            return compileOption(linkList, (Option)expression);
        }
        else if (expression instanceof Reference) {
            return compileReference(linkList, (Reference)expression);
        }
        else if (expression instanceof Repeat) {
            return compileRepeat(linkList, (Repeat)expression);
        }
        else if (expression instanceof Sequence) {
            return compileSequence(linkList, (Sequence)expression);
        }
        else if (expression instanceof Literal) {
            return compileLiteral(linkList, (Literal)expression);
        }
        else if (expression instanceof Wildcard) {
            return compileWildcard(linkList, (Wildcard)expression);
        }
        else if (expression instanceof Wrapping) {
            return compileWrapping(linkList, (Wrapping)expression);
        }
        else {
            throw ErrorFactory.internalError("not implemented expression: " + expression);
        }
    }

    private Segment compileAlternation(LinkList linkList, Alternation alternation) {
        var sources = new LinkedHashSet<Node>();
        var targets = new LinkedHashSet<Node>();

        for (var item : alternation.items) {
            var itemMachine = compileExpression(linkList, item);

            sources.addAll(itemMachine.sources().toCollection());
            targets.addAll(itemMachine.targets().toCollection());
        }

        return new Segment(NodeSet.of(sources), NodeSet.of(targets));
    }

    private Segment compileOption(LinkList linkList, Option option) {
        var machine = compileExpression(linkList, option.content);
        return new Segment(
                machine.sources(),
                machine.sources().join(machine.targets()));
    }

    private Segment compileReference(LinkList linkList, Reference reference) {
        var source = nodeFactory.createNode();
        var target = nodeFactory.createNode();
        var pattern = PatternFactory.reference(reference.name);

        linkList.createLink(source, target, pattern);

        return new Segment(NodeSet.of(source), NodeSet.of(target));
    }

    private Segment compileRepeat(LinkList linkList, Repeat repeat) {
        var source = nodeFactory.createNode();
        var target = nodeFactory.createNode();

        var contentMachine = compileExpression(linkList, repeat.content);

        linkList.createLink(source, contentMachine.sources());
        linkList.createLink(contentMachine.targets(), target);

        if (repeat.separator != null) {
            var separatorMachine = compileExpression(linkList, repeat.separator);

            linkList.createLink(contentMachine.targets(), separatorMachine.sources());
            linkList.createLink(separatorMachine.targets(), contentMachine.sources());
        }
        else {
            linkList.createLink(contentMachine.targets(), contentMachine.sources());
        }

        return new Segment(NodeSet.of(source), NodeSet.of(target));
    }

    private Segment compileSequence(LinkList linkList, Sequence sequence) {
        var source = nodeFactory.createNode();
        var lastNode = source;

        for (var item : sequence.items) {
            var itemMachine = compileExpression(linkList, item);

            linkList.createLink(lastNode, itemMachine.sources());

            lastNode = nodeFactory.createNode();

            linkList.createLink(itemMachine.targets(), lastNode);
        }

        return new Segment(NodeSet.of(source), NodeSet.of(lastNode));
    }

    private Segment compileLiteral(LinkList linkList, Literal literal) {
        var source = nodeFactory.createNode();
        var target = nodeFactory.createNode();

        linkList.createLink(source, target, literal.pattern);

        return new Segment(NodeSet.of(source), NodeSet.of(target));
    }

    private Segment compileWildcard(LinkList linkList, Wildcard wildcard) {
        throw new UnsupportedOperationException();
    }

    private Segment compileWrapping(LinkList linkList, Wrapping wrapping) {
        var machine = compileExpressionClean(wrapping.content);
        var group = operationFactory.nextGroup();
        var begin = operationFactory.createBegin(wrapping.type, group, wrapping.argument);
        var end = operationFactory.createEnd(wrapping.type, group, wrapping.argument);

        for (var link : machine.links()) {
            if (machine.source() == link.getSource()) {
                link.prependBeginOperation(begin);
            }

            if (machine.targets().contains(link.getTarget())) {
                link.appendEndOperation(end);
            }
        }

        linkList.addLinks(machine.links());

        return new Segment(NodeSet.of(machine.source()), machine.targets());
    }

}
