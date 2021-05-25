package org.gramat.pipeline;

import org.gramat.actions.ActionFactory;
import org.gramat.data.nodes.Nodes;
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
import org.gramat.graphs.CleanMachine;
import org.gramat.graphs.CleanMachineProgram;
import org.gramat.graphs.DirtyMachine;
import org.gramat.graphs.DirtySegment;
import org.gramat.graphs.NodeProvider;
import org.gramat.graphs.links.LinkProvider;
import org.gramat.symbols.SymbolFactory;

import java.util.LinkedHashMap;

public class ExpressionCompiler {

    public static CleanMachineProgram run(NodeProvider nodeProvider, ExpressionProgram program) {
        var dependencies = new LinkedHashMap<String, CleanMachine>();
        var main = compileExpressionClean(nodeProvider, program.main);

        for (var entry : program.dependencies.entrySet()) {
            var dependency = compileExpressionClean(nodeProvider, entry.getValue());

            dependencies.put(entry.getKey(), dependency);
        }

        return new CleanMachineProgram(main, dependencies);
    }

    private static CleanMachine compileExpressionClean(NodeProvider nodeProvider, Expression expression) {
        var linkProvider = new LinkProvider();
        var dirtySegment = compileExpression(nodeProvider, linkProvider, expression);

        return MachineCompiler.compile(
                nodeProvider,
                new DirtyMachine(dirtySegment.sources(), dirtySegment.targets(), linkProvider.toList()));
    }

    private static DirtySegment compileExpression(NodeProvider nodeProvider, LinkProvider linkProvider, Expression expression) {
        if (expression instanceof Alternation) {
            return compileAlternation(nodeProvider, linkProvider, (Alternation)expression);
        }
        else if (expression instanceof Option) {
            return compileOption(nodeProvider, linkProvider, (Option)expression);
        }
        else if (expression instanceof Reference) {
            return compileReference(nodeProvider, linkProvider, (Reference)expression);
        }
        else if (expression instanceof Repeat) {
            return compileRepeat(nodeProvider, linkProvider, (Repeat)expression);
        }
        else if (expression instanceof Sequence) {
            return compileSequence(nodeProvider, linkProvider, (Sequence)expression);
        }
        else if (expression instanceof Literal) {
            return compileLiteral(nodeProvider, linkProvider, (Literal)expression);
        }
        else if (expression instanceof Wildcard) {
            return compileWildcard(nodeProvider, linkProvider, (Wildcard)expression);
        }
        else if (expression instanceof Wrapping) {
            return compileWrapping(nodeProvider, linkProvider, (Wrapping)expression);
        }
        else {
            throw ErrorFactory.internalError("not implemented expression: " + expression);
        }
    }

    private static DirtySegment compileAlternation(NodeProvider nodeProvider, LinkProvider linkProvider, Alternation alternation) {
        var sources = Nodes.createW();
        var targets = Nodes.createW();

        for (var item : alternation.items) {
            var itemMachine = compileExpression(nodeProvider, linkProvider, item);

            sources.addAll(itemMachine.targets());
            targets.addAll(itemMachine.targets());
        }

        return new DirtySegment(sources, targets);
    }

    private static DirtySegment compileOption(NodeProvider nodeProvider, LinkProvider linkProvider, Option option) {
        var machine = compileExpression(nodeProvider, linkProvider, option);
        return new DirtySegment(
                machine.sources(),
                Nodes.join(machine.sources(), machine.targets()));
    }

    private static DirtySegment compileReference(NodeProvider nodeProvider, LinkProvider linkProvider, Reference reference) {
        var source = nodeProvider.createNode();
        var target = nodeProvider.createNode();
        var symbol = SymbolFactory.reference(reference.name);

        linkProvider.createLink(source, target, symbol);

        return new DirtySegment(Nodes.of(source), Nodes.of(target));
    }

    private static DirtySegment compileRepeat(NodeProvider nodeProvider, LinkProvider linkProvider, Repeat repeat) {
        var source = nodeProvider.createNode();
        var target = nodeProvider.createNode();

        var contentMachine = compileExpression(nodeProvider, linkProvider, repeat.content);

        linkProvider.createLink(source, contentMachine.sources());
        linkProvider.createLink(contentMachine.targets(), target);

        if (repeat.separator != null) {
            var separatorMachine = compileExpression(nodeProvider, linkProvider, repeat.separator);

            linkProvider.createLink(contentMachine.targets(), separatorMachine.sources());
            linkProvider.createLink(separatorMachine.targets(), contentMachine.sources());
        }
        else {
            linkProvider.createLink(contentMachine.targets(), contentMachine.sources());
        }

        return new DirtySegment(Nodes.of(source), Nodes.of(target));
    }

    private static DirtySegment compileSequence(NodeProvider nodeProvider, LinkProvider linkProvider, Sequence sequence) {
        var source = nodeProvider.createNode();
        var lastNode = source;

        for (var item : sequence.items) {
            var itemMachine = compileExpression(nodeProvider, linkProvider, item);

            linkProvider.createLink(lastNode, itemMachine.sources());

            lastNode = nodeProvider.createNode();

            linkProvider.createLink(itemMachine.targets(), lastNode);
        }

        return new DirtySegment(Nodes.of(source), Nodes.of(lastNode));
    }

    private static DirtySegment compileLiteral(NodeProvider nodeProvider, LinkProvider linkProvider, Literal literal) {
        var source = nodeProvider.createNode();
        var target = nodeProvider.createNode();

        linkProvider.createLink(source, target, literal.symbol);

        return new DirtySegment(Nodes.of(source), Nodes.of(target));
    }

    private static DirtySegment compileWildcard(NodeProvider nodeProvider, LinkProvider linkProvider, Wildcard wildcard) {
        throw new UnsupportedOperationException();
    }

    private static DirtySegment compileWrapping(NodeProvider nodeProvider, LinkProvider linkProvider, Wrapping wrapping) {
        var machine = compileExpressionClean(nodeProvider, wrapping);
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

        linkProvider.addLinks(machine.links());

        return new DirtySegment(Nodes.of(machine.source()), machine.targets());
    }

    private ExpressionCompiler() {}

}
