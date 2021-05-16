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
import org.gramat.graphs.Link;
import org.gramat.graphs.LinkAction;
import org.gramat.graphs.Machine;
import org.gramat.graphs.MachineProgram;
import org.gramat.graphs.Node;
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
        var target = graph.createNode();

        compileExpression(graph, expression, source, target);

        return new Machine(source, target, graph.links);
    }

    private void compileExpression(Graph graph, Expression expression, Node source, Node target) {
        if (expression instanceof Wrapping) {
            compileWrapping(graph, (Wrapping)expression, source, target);
        }
        else if (expression instanceof Alternation) {
            compileAlternation(graph, (Alternation)expression, source, target);
        }
        else if (expression instanceof Option) {
            compileOption(graph, (Option)expression, source, target);
        }
        else if (expression instanceof Reference) {
            compileReference(graph, (Reference)expression, source, target);
        }
        else if (expression instanceof Repeat) {
            compileRepeat(graph, (Repeat)expression, source, target);
        }
        else if (expression instanceof Sequence) {
            compileSequence(graph, (Sequence)expression, source, target);
        }
        else if (expression instanceof Literal) {
            compileLiteral(graph, (Literal)expression, source, target);
        }
        else if (expression instanceof Wildcard) {
            compileWildcard(graph, (Wildcard)expression, source, target);
        }
        else {
            throw ErrorFactory.internalError("not implemented expression: " + expression);
        }
    }

    private void compileWrapping(Graph graph, Wrapping wrapping, Node source, Node target) {
        var initialLinks = DataUtils.copy(graph.links);

        compileExpression(graph, wrapping.content, source, target);

        // Compute links created by the compiled expression
        var newLinks = DataUtils.copy(graph.links);
        newLinks.removeAll(initialLinks);

        var beginAction = wrapping.createBeginAction();
        var endAction = wrapping.createEndAction();
        var ignoreBeginAction = ActionFactory.ignore(beginAction);
        var cancelEndAction = ActionFactory.cancel(endAction);

        var sources = Link.forwardClosure(source, newLinks);
        var targets = Link.backwardClosure(target, newLinks);

        for (var link : newLinks) {
            if (link instanceof LinkAction linkAct) {
                var fromSource = sources.contains(linkAct.source);
                var fromTarget = targets.contains(linkAct.source);
                var toSource = sources.contains(linkAct.target);
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
    }

    private void compileAlternation(Graph graph, Alternation alternation, Node source, Node target) {
        for (var item : alternation.items) {
            var aux = graph.createNode();

            compileExpression(graph, item, source, aux);

            graph.createLink(aux, target);
        }
    }

    private void compileOption(Graph graph, Option option, Node source, Node target) {
        compileExpression(graph, option.content, source, target);

        graph.createLink(source, target);
    }

    private void compileReference(Graph graph, Reference reference, Node source, Node target) {
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

        graph.createLink(source, target, refMap.newName, null, null, null);
    }

    private void compileRepeat(Graph graph, Repeat repeat, Node source, Node target) {
        compileExpression(graph, repeat.content, source, target);

        if (repeat.separator != null) {
            var aux = graph.createNode();

            compileExpression(graph, repeat.separator, target, aux);
            compileExpression(graph, repeat.content, aux, target);
        }
        else {
            compileExpression(graph, repeat.content, target, target);
        }
    }

    private void compileSequence(Graph graph, Sequence sequence, Node source, Node target) {
        var length = sequence.items.size();
        var lastIndex = length - 1;
        var lastNode = source;

        for (var i = 0 ; i < length; i++) {
            var item = sequence.items.get(i);
            if (i == lastIndex) {
                compileExpression(graph, item, lastNode, target);

                lastNode = target;
            }
            else {
                var aux = graph.createNode();

                compileExpression(graph, item, lastNode, aux);

                lastNode = aux;
            }
        }
    }

    private void compileLiteral(Graph graph, Literal literal, Node source, Node target) {
        graph.createLink(source, target, literal.symbol);
    }

    private void compileWildcard(Graph graph, Wildcard wildcard, Node source, Node target) {
        if (wildcard.level != 1) {
            throw ErrorFactory.syntaxError(wildcard.location,
                    "No supported wilcard level: " + wildcard.level);
        }

        // TODO improve how to make wildcards 🤔
        source.wildcard = true;
        target.wildcard = true;
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

