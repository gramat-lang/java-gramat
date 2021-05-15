package org.gramat.pipeline;

import lombok.extern.slf4j.Slf4j;
import org.gramat.actions.ActionFactory;
import org.gramat.errors.ErrorFactory;
import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionProgram;
import org.gramat.expressions.Alternation;
import org.gramat.expressions.Literal;
import org.gramat.expressions.Option;
import org.gramat.expressions.Reference;
import org.gramat.expressions.Repeat;
import org.gramat.expressions.Sequence;
import org.gramat.expressions.Wildcard;
import org.gramat.expressions.Wrapping;
import org.gramat.machines.Graph;
import org.gramat.machines.Machine;
import org.gramat.machines.MachineProgram;
import org.gramat.machines.Node;
import org.gramat.machines.NodeSet;
import org.gramat.machines.RecursionSymbol;
import org.gramat.symbols.Symbol;
import org.gramat.tools.IdentifierProvider;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ExpressionCompiler {

    public static MachineProgram run(ExpressionProgram program) {
        return new ExpressionCompiler(program.dependencies).run(program.main);
    }

    private final IdentifierProvider graphIds;
    private final IdentifierProvider recursionIds;
    private final Map<String, Expression> dependencies;
    private final Deque<ReferenceMap> referenceStack;
    private final Map<String, Machine> newDependencies;

    public ExpressionCompiler(Map<String, Expression> dependencies) {
        this.dependencies = dependencies;
        this.graphIds = IdentifierProvider.create(1);
        this.recursionIds = IdentifierProvider.create(1);
        this.referenceStack = new ArrayDeque<>();
        this.newDependencies = new LinkedHashMap<>();
    }

    public MachineProgram run(Expression main) {
        var newMain = compileMachine(main);

        return new MachineProgram(newMain, newDependencies);
    }

    public Machine compileMachine(Expression expression) {
        var graph = new Graph(graphIds);
        var source = graph.createNode();
        var targets = compileExpression(expression, graph, source, Set.of(graph.createNode()));
        return new Machine(source, targets, graph.links);
    }

    private Set<Node> compileExpression(Expression expression, Graph graph, Node source, Set<Node> targets) {
        if (targets.isEmpty()) {
            throw ErrorFactory.internalError("empty node set");
        }

        if (expression instanceof Wrapping) {
            return compileWrapping((Wrapping)expression, graph, source, targets);
        }
        else if (expression instanceof Alternation) {
            return compileAlternation((Alternation)expression, graph, source, targets);
        }
        else if (expression instanceof Option) {
            return compileOption((Option)expression, graph, source, targets);
        }
        else if (expression instanceof Reference) {
            return compileReference((Reference)expression, graph, source, targets);
        }
        else if (expression instanceof Repeat) {
            return compileRepeat((Repeat)expression, graph, source, targets);
        }
        else if (expression instanceof Sequence) {
            return compileSequence((Sequence)expression, graph, source, targets);
        }
        else if (expression instanceof Literal) {
            return compileLiteral((Literal)expression, graph, source, targets);
        }
        else if (expression instanceof Wildcard) {
            return compileWildcard((Wildcard)expression, source, targets);
        }
        else {
            throw ErrorFactory.internalError("not implemented expression: " + expression);
        }
    }

    private Set<Node> compileExpression(Expression expression, Graph graph, Set<Node> sources, Set<Node> targets) {
        var result = new NodeSet();

        for (var source : sources) {
            result.addAll(compileExpression(expression, graph, source, targets));
        }

        return result;
    }

    private Set<Node> compileWrapping(Wrapping wrapping, Graph graph, Node source, Set<Node> targets) {
        var result = compileExpression(wrapping.content, graph, source, targets);
        var beginAction = wrapping.createBeginAction();
        var endAction = wrapping.createEndAction();
        var ignoreBeginAction = ActionFactory.ignore(beginAction);
        var cancelEndAction = ActionFactory.cancel(endAction);

        for (var link : graph.links) {
            if (link.source == source) {
                link.beginActions.add(beginAction);
            }

            if (link.target == source) {
                link.beginActions.add(ignoreBeginAction);
            }

            if (targets.contains(link.target)) {
                link.endActions.add(endAction);
            }

            if (targets.contains(link.source)) {
                link.beginActions.add(cancelEndAction);
            }
        }

        // TODO consider link S->S, T->T, S->T, etc...

        return result;
    }

    private Set<Node> compileAlternation(Alternation alternation, Graph graph, Node source, Set<Node> targets) {
        var result = new NodeSet();

        for (var item : alternation.items) {
            var itemTargets = compileExpression(item, graph, source, targets);

            result.addAll(itemTargets);
        }

        return result;
    }

    private Set<Node> compileOption(Option option, Graph graph, Node source, Set<Node> targets) {
        var result = compileExpression(option.content, graph, source, targets);

        result.add(source);

        return result;
    }

    private Set<Node> compileReference(Reference reference, Graph graph, Node source, Set<Node> targets) {
        var refMap = searchReferenceMap(reference.name);

        Symbol symbol;

        if (refMap != null) {
            symbol = new RecursionSymbol(refMap.newName);
        }
        else {
            var id = recursionIds.next();
            var newName = String.format("%s-%s", reference.name, id);

            symbol = new RecursionSymbol(newName);

            compileDependency(reference.name, newName);
        }

        for (var target : targets) {
            graph.createLink(source, target, symbol, null);
        }

        return targets;
    }

    private void compileDependency(String oldName, String newName) {
        var dependency = dependencies.get(oldName);
        if (dependency == null) {
            throw ErrorFactory.notFound(oldName);
        }

        log.debug("Compiling {} -> {}", oldName, newName);

        referenceStack.addFirst(new ReferenceMap(oldName, newName));

        var newDependency = compileMachine(dependency);

        newDependencies.put(newName, newDependency);

        referenceStack.removeFirst();
    }

    private Set<Node> compileRepeat(Repeat repeat, Graph graph, Node source, Set<Node> targets) {
        var result = new NodeSet();
        var contentTargets = compileExpression(repeat.content, graph, source, targets);

        result.addAll(contentTargets);

        if (repeat.separator != null) {
            var separatorTargets = compileExpression(repeat.separator, graph, contentTargets, Set.of(graph.createNode()));
            var loopTargets = compileExpression(repeat.content, graph, separatorTargets, contentTargets);

            result.addAll(loopTargets);
        }
        else {
            var loopTargets = compileExpression(repeat.content, graph, contentTargets, contentTargets);

            result.addAll(loopTargets);
        }

        return result;
    }

    private Set<Node> compileSequence(Sequence sequence, Graph graph, Node source, Set<Node> targets) {
        var lastNodes = Set.of(source);

        for (var i = 0 ; i < sequence.items.size(); i++) {
            var item = sequence.items.get(i);

            if (i < sequence.items.size() - 1) {
                lastNodes = compileExpression(item, graph, lastNodes, Set.of(graph.createNode()));
            }
            else {
                // only the last item
                lastNodes = compileExpression(item, graph, lastNodes, targets);
            }
        }

        return lastNodes;
    }

    private Set<Node> compileLiteral(Literal literal, Graph graph, Node source, Set<Node> targets) {
        for (var target : targets) {
            graph.createLink(source, target, literal.symbol, null);
        }

        return targets;
    }

    private Set<Node> compileWildcard(Wildcard wildcard, Node source, Set<Node> targets) {
        if (wildcard.level != 1) {
            throw ErrorFactory.syntaxError(wildcard.location,
                    "No supported wilcard level: " + wildcard.level);
        }

        source.wildcard = true;

        for (var target : targets) {
            target.wildcard = true;
        }

        return targets;
    }

    private ReferenceMap searchReferenceMap(String name) {
        for (var item : referenceStack) {
            if (item.oldName.equals(name)) {
                return item;
            }
        }
        return null;
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

