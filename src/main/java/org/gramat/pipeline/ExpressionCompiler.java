package org.gramat.pipeline;

import lombok.extern.slf4j.Slf4j;
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
import org.gramat.graphs.Graph;
import org.gramat.graphs.Machine;
import org.gramat.graphs.Node;
import org.gramat.graphs.Segment;
import org.gramat.tools.IdentifierProvider;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class ExpressionCompiler {

    public static Machine run(ExpressionProgram program) {
        return new ExpressionCompiler(program.dependencies).run(program.main);
    }

    private final Graph graph;
    private final Map<String, Expression> dependencies;
    private final Map<String, Segment> referenceSegments;

    public ExpressionCompiler(Map<String, Expression> dependencies) {
        this.dependencies = dependencies;
        this.graph = new Graph(IdentifierProvider.create(1));
        this.referenceSegments = new LinkedHashMap<>();
    }

    public Machine run(Expression main) {
        log.debug("Compiling main machine...");

        var mainMachine = compileMachine(main);

        log.debug("Compilation completed");  // TODO add more debug info

        return mainMachine;
    }

    public Machine compileMachine(Expression expression) {
        var source = graph.createNode();
        var target = graph.createNode();

        compileExpression(expression, source, target);

        return new Machine(source, target, graph.links);
    }

    private void compileExpression(Expression expression, Node source, Node target) {
        if (expression instanceof Wrapping) {
            compileWrapping((Wrapping)expression, source, target);
        }
        else if (expression instanceof Alternation) {
            compileAlternation((Alternation)expression, source, target);
        }
        else if (expression instanceof Option) {
            compileOption((Option)expression, source, target);
        }
        else if (expression instanceof Reference) {
            compileReference((Reference)expression, source, target);
        }
        else if (expression instanceof Repeat) {
            compileRepeat((Repeat)expression, source, target);
        }
        else if (expression instanceof Sequence) {
            compileSequence((Sequence)expression, source, target);
        }
        else if (expression instanceof Literal) {
            compileLiteral((Literal)expression, source, target);
        }
        else if (expression instanceof Wildcard) {
            compileWildcard((Wildcard)expression, source, target);
        }
        else {
            throw ErrorFactory.internalError("not implemented expression: " + expression);
        }
    }

    private void compileWrapping(Wrapping wrapping, Node source, Node target) {
        var links0 = graph.links.copyR();

        compileExpression(wrapping.content, source, target);

        var links = graph.links.copyW();
        links.removeAll(links0);

        var begin = ActionFactory.createBeginAction(wrapping.type);
        var end = ActionFactory.createEndAction(wrapping.type, wrapping.argument);

        for (var link : links) {
            if (link.source == source) {
                link.beforeActions.prepend(begin);
            }

            if (link.target == target) {
                link.afterActions.append(end);
            }
        }
    }

    private void compileAlternation(Alternation alternation, Node source, Node target) {
        for (var item : alternation.items) {
            var itemTarget = graph.createNode();

            compileExpression(item, source, itemTarget);

            graph.createLink(itemTarget, target);
        }
    }

    private void compileOption(Option option, Node source, Node target) {
        compileExpression(option.content, source, target);

        graph.createLink(source, target);
    }

    private void compileReference(Reference reference, Node source, Node target) {
        var segment = getOrCreateSegment(reference.name);

        log.debug("Connecting segment {}...", reference.name);

        graph.createLink(source, segment.source);
        graph.createLink(segment.target, target);
    }

    private Segment getOrCreateSegment(String name) {
        var segment = referenceSegments.get(name);
        if (segment != null) {
            return segment;
        }

        log.debug("Creating segment {}...", name);

        var dependency = dependencies.get(name);
        if (dependency == null) {
            throw ErrorFactory.notFound(name);
        }

        var refSource = graph.createNode();
        var refTarget = graph.createNode();

        segment = new Segment(refSource, refTarget);

        referenceSegments.put(name, segment);

        compileExpression(dependency, refSource, refTarget);

        return segment;
    }

    private void compileRepeat(Repeat repeat, Node source, Node target) {
        var contentSource = graph.createNode();
        var contentTarget = graph.createNode();

        compileExpression(repeat.content, contentSource, contentTarget);

        if (repeat.separator != null) {
            compileExpression(repeat.separator, contentTarget, contentSource);
        }
        else {
            graph.createLink(contentTarget, contentSource);
        }

        graph.createLink(source, contentSource);
        graph.createLink(contentTarget, target);
    }

    private void compileSequence(Sequence sequence, Node source, Node target) {
        var lastNode = source;

        for (var i = 0; i < sequence.items.size(); i++) {
            var item = sequence.items.get(i);

            Node itemTarget;
            if (i == sequence.items.size() - 1) {
                itemTarget = target;
            }
            else {
                itemTarget = graph.createNode();
            }

            compileExpression(item, lastNode, itemTarget);

            lastNode = itemTarget;
        }
    }

    private void compileLiteral(Literal literal, Node source, Node target) {
        graph.createLink(source, target, literal.symbol);
    }

    private void compileWildcard(Wildcard wildcard, Node source, Node target) {
        if (wildcard.level != 1) {
            throw ErrorFactory.syntaxError(wildcard.location,
                    "No supported wilcard level: " + wildcard.level);
        }

        // TODO improve how to make wildcards 🤔
        throw new UnsupportedOperationException();
    }

}

