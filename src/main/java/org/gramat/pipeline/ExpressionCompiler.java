package org.gramat.pipeline;

import lombok.extern.slf4j.Slf4j;
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
import org.gramat.graphs.Graph;
import org.gramat.graphs.links.Link;
import org.gramat.graphs.links.LinkAction;
import org.gramat.graphs.Machine;
import org.gramat.graphs.MachineProgram;
import org.gramat.graphs.Node;
import org.gramat.symbols.SymbolFactory;
import org.gramat.tools.DataUtils;
import org.gramat.tools.IdentifierProvider;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class ExpressionCompiler {

    public static MachineProgram run(ExpressionProgram program) {
        return new ExpressionCompiler(program.dependencies).run(program.main);
    }

    private final IdentifierProvider referenceIds;
    private final IdentifierProvider graphIds;
    private final Map<String, Expression> dependencies;
    private final Deque<ReferenceMap> referenceStack;
    private final Map<String, Machine> newDependencies;

    public ExpressionCompiler(Map<String, Expression> dependencies) {
        this.dependencies = dependencies;
        this.referenceIds = IdentifierProvider.create(1);
        this.graphIds = IdentifierProvider.create(1);
        this.referenceStack = new ArrayDeque<>();
        this.newDependencies = new LinkedHashMap<>();
    }

    public MachineProgram run(Expression main) {
        log.debug("Compiling main machine...");

        var newMain = compileMachine(main);

        log.debug("Compilation completed: {} machine(s)", 1 + newDependencies.size());

        return new MachineProgram(newMain, newDependencies);
    }

    public Machine compileMachine(Expression expression) {
        var graph = new Graph(graphIds);
        var source = graph.createNode();
        var targets = compileExpression(graph, expression, source);
        return new Machine(source, targets, graph.links);
    }

    private Nodes compileExpression(Graph graph, Expression expression, Nodes sources) {
        var result = Nodes.createW();

        for (var source : sources) {
            result.addAll(compileExpression(graph, expression, source));
        }

        return result;
    }

    private Nodes compileExpression(Graph graph, Expression expression, Node source) {
        if (expression instanceof Wrapping) {
            return compileWrapping(graph, (Wrapping)expression, source);
        }
        else if (expression instanceof Alternation) {
            return compileAlternation(graph, (Alternation)expression, source);
        }
        else if (expression instanceof Option) {
            return compileOption(graph, (Option)expression, source);
        }
        else if (expression instanceof Reference) {
            return compileReference(graph, (Reference)expression, source);
        }
        else if (expression instanceof Repeat) {
            return compileRepeat(graph, (Repeat)expression, source);
        }
        else if (expression instanceof Sequence) {
            return compileSequence(graph, (Sequence)expression, source);
        }
        else if (expression instanceof Literal) {
            return compileLiteral(graph, (Literal)expression, source);
        }
        else if (expression instanceof Wildcard) {
            return compileWildcard(graph, (Wildcard)expression, source);
        }
        else {
            throw ErrorFactory.internalError("not implemented expression: " + expression);
        }
    }

    private Nodes compileWrapping(Graph graph, Wrapping wrapping, Node source) {
        var initialLinks = graph.links.copyW();
        var targets = compileExpression(graph, wrapping.content, source);

        // Compute links created by the compiled expression
        var newLinks = graph.links.copyW();
        newLinks.removeAll(initialLinks);

        var beginAction = wrapping.createBeginAction();
        var endAction = wrapping.createEndAction();
        var ignoreBeginAction = ActionFactory.ignore(beginAction);
        var cancelEndAction = ActionFactory.cancel(endAction);

        for (var link : newLinks) {
            if (link instanceof LinkAction linkAct) {
                var fromSource = (source == linkAct.source);
                var fromTarget = targets.contains(linkAct.source);
                var toSource = (source == linkAct.target);
                var toTarget = targets.contains(linkAct.target);

                if (fromSource) {
                    linkAct.beginActions.append(beginAction);
                }

                if (toTarget) {
                    linkAct.endActions.prepend(endAction);
                }

                if (fromTarget) {
                    linkAct.beginActions.prepend(cancelEndAction);
                }

                if (toSource) {
                    linkAct.beginActions.append(ignoreBeginAction);
                }
            }
        }

        return targets;
    }

    private Nodes compileAlternation(Graph graph, Alternation alternation, Node source) {
        var result = Nodes.createW();

        for (var item : alternation.items) {
            var itemTargets = compileExpression(graph, item, source);

            result.addAll(itemTargets);
        }

        return result;
    }

    private Nodes compileOption(Graph graph, Option option, Node source) {
        var targets = compileExpression(graph, option.content, source);

        return Nodes.join(targets, source);
    }

    private Nodes compileReference(Graph graph, Reference reference, Node source) {
        ReferenceMap refMap = null;
        for (var item : referenceStack) {
            if (item.oldName.equals(reference.name)) {
                refMap = item;
                break;
            }
        }
        if (refMap == null) {
            var id = referenceIds.next();
            var newName = String.format("%s-%s", reference.name, id);
            var dependency = dependencies.get(reference.name);
            if (dependency == null) {
                throw ErrorFactory.notFound(reference.name);
            }

            refMap = new ReferenceMap(reference.name, newName);

            log.debug("Compiling {} dependency (from {})...", newName, reference.name);

            referenceStack.addFirst(refMap);
            var newDependency = compileMachine(dependency);
            referenceStack.removeFirst();

            newDependencies.put(newName, newDependency);
        }

        var target = graph.createNode();

        graph.createLink(source, target,
                SymbolFactory.reference(refMap.newName),
                null, null);

        return Nodes.of(target);
    }

    private Nodes compileRepeat(Graph graph, Repeat repeat, Node source) {
        var targets = compileExpression(graph, repeat.content, source);

        if (repeat.separator != null) {
            var separatorTargets = compileExpression(graph, repeat.separator, targets);

            graph.createLink(separatorTargets, source, null, null);
        }
        else {
            graph.createLink(targets, source, null, null);
        }

        return targets;
    }

    private Nodes compileSequence(Graph graph, Sequence sequence, Node source) {
        var targets = Nodes.of(source);

        for (var item : sequence.items) {
            targets = compileExpression(graph, item, targets);
        }

        return targets;
    }

    private Nodes compileLiteral(Graph graph, Literal literal, Node source) {
        var target = graph.createNode();

        graph.createLink(source, target, literal.symbol);

        return Nodes.of(target);
    }

    private Nodes compileWildcard(Graph graph, Wildcard wildcard, Node source) {
        if (wildcard.level != 1) {
            throw ErrorFactory.syntaxError(wildcard.location,
                    "No supported wilcard level: " + wildcard.level);
        }

        // TODO improve how to make wildcards 🤔
        throw new UnsupportedOperationException();
    }

    private static class ReferenceMap {
        public final String oldName;
        public final String newName;

        public ReferenceMap(String oldName, String newName) {
            this.oldName = oldName;
            this.newName = newName;
        }
    }

}

