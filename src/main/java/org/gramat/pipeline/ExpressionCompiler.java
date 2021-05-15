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
import org.gramat.machines.Graph;
import org.gramat.machines.Link;
import org.gramat.machines.LinkAction;
import org.gramat.machines.Machine;
import org.gramat.machines.MachineProgram;
import org.gramat.machines.Node;
import org.gramat.tools.DataUtils;
import org.gramat.tools.IdentifierProvider;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExpressionCompiler {

    public static MachineProgram run(ExpressionProgram program) {
        return new ExpressionCompiler(program.dependencies).run(program.main);
    }

    private final Graph graph;
    private final IdentifierProvider referenceIds;
    private final Map<String, Expression> dependencies;
    private final Deque<ReferenceMap> referenceStack;
    private final Map<String, Machine> newDependencies;

    public ExpressionCompiler(Map<String, Expression> dependencies) {
        this.dependencies = dependencies;
        this.graph = new Graph();
        this.referenceIds = IdentifierProvider.create(1);
        this.referenceStack = new ArrayDeque<>();
        this.newDependencies = new LinkedHashMap<>();
    }

    public MachineProgram run(Expression main) {
        var newMain = compileMachine(main);

        return new MachineProgram(newMain, newDependencies);
    }

    public Machine compileMachine(Expression expression) {
        var source = graph.createNode();
        var target = graph.createNode();

        compileExpression(expression, source, target);

        return new Machine(source, target, graph.links);
    }

    private List<Link> compileExpression(Expression expression, Node source, Node target) {
        var initialLinks = DataUtils.copy(graph.links);

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

        // Compute links created by the compiled expression
        var result = DataUtils.copy(graph.links);
        result.removeAll(initialLinks);
        return result;
    }

    private void compileWrapping(Wrapping wrapping, Node source, Node target) {
        var newLinks = compileExpression(wrapping.content, source, target);
        var beginAction = wrapping.createBeginAction();
        var endAction = wrapping.createEndAction();
        var ignoreBeginAction = ActionFactory.ignore(beginAction);
        var cancelEndAction = ActionFactory.cancel(endAction);

        for (var link : newLinks) {
            if (link instanceof LinkAction) {
                var linkAct = (LinkAction) link;
                if (linkAct.source == source) {
                    linkAct.beginActions.add(beginAction);
                }

                if (linkAct.target == source) {
                    linkAct.beginActions.add(ignoreBeginAction);
                }

                if (linkAct.target == target) {
                    linkAct.endActions.add(endAction);
                }

                if (linkAct.source == target) {
                    linkAct.beginActions.add(cancelEndAction);
                }
            }
        }
    }

    private void compileAlternation(Alternation alternation, Node source, Node target) {
        for (var item : alternation.items) {
            compileExpression(item, source, target);
        }
    }

    private void compileOption(Option option, Node source, Node target) {
        compileExpression(option.content, source, target);

        graph.createLink(source, target);  // This is the origin of ALL empty links
    }

    private void compileReference(Reference reference, Node source, Node target) {
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

            log.debug("Compiling {} -> {}", reference.name, newName);

            referenceStack.addFirst(refMap);
            var newDependency = compileMachine(dependency);
            referenceStack.removeFirst();

            newDependencies.put(newName, newDependency);
        }

        graph.createLink(source, target, refMap.newName, null, null, null);
    }

    private void compileRepeat(Repeat repeat, Node source, Node target) {
        compileExpression(repeat.content, source, target);

        if (repeat.separator != null) {
            var aux = graph.createNode();

            compileExpression(repeat.separator, target, aux);
            compileExpression(repeat.content, aux, target);
        }
        else {
            compileExpression(repeat.content, target, target);
        }
    }

    private void compileSequence(Sequence sequence, Node source, Node target) {
        var length = sequence.items.size();
        var lastIndex = length - 1;
        var lastNode = source;

        for (var i = 0 ; i < length; i++) {
            var item = sequence.items.get(i);
            if (i == lastIndex) {
                var aux = graph.createNode();

                compileExpression(item, lastNode, aux);

                lastNode = aux;
            }
            else {
                // only the last item
                compileExpression(item, lastNode, target);

                lastNode = target;
            }
        }
    }

    private void compileLiteral(Literal literal, Node source, Node target) {
        graph.createLink(source, target, literal.symbol, null);
    }

    private void compileWildcard(Wildcard wildcard, Node source, Node target) {
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

